import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { PaginatorModule } from 'primeng/paginator';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-fleet-ledger',
  standalone: true,
  imports: [CommonModule, PaginatorModule],
  templateUrl: './fleet-ledger.html',
  styleUrl: './fleet-ledger.scss'
})
export class FleetLedger implements OnInit {
  
  private http = inject(HttpClient);
  private messageService = inject(MessageService);
  
  // State Management Signals
  vehicles = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  
  // Pagination & Search State
  totalRecords = signal<number>(0);
  first = 0;
  rows = 10;
  
  searchQuery = signal<string>('');
  private searchTimeout: any;

  ngOnInit() {
    this.fetchLedgerData(0, this.rows);
  }

  // =========================================================================
  // SEARCH PROTOCOL WITH DEBOUNCE
  // =========================================================================
  updateSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    if (this.searchTimeout) clearTimeout(this.searchTimeout);
    
    // Wait 400ms after the user stops typing to execute the search
    this.searchTimeout = setTimeout(() => {
      this.searchQuery.set(input.value);
      this.first = 0; // Reset to page 1
      this.fetchLedgerData(0, this.rows);
    }, 400);
  }

  fetchLedgerData(page: number, size: number) {
    this.isLoading.set(true);
    const query = encodeURIComponent(this.searchQuery().trim());
    
    // Ensure your backend controller accepts the 'search' parameter!
    const url = `${environment.apiUrl}/car/getAllCars?page=${page}&size=${size}&search=${query}`;
    
    this.http.get<any>(url).subscribe({
      next: (res) => {
        setTimeout(() => {
          const data = res.data?.content || res.data || [];
          this.vehicles.set(data);
          this.totalRecords.set(res.data?.totalElements || data.length);
          this.isLoading.set(false);
        }, 800); 
      },
      error: (err) => {
        console.error('Failed to decrypt ledger data:', err);
        setTimeout(() => {
          this.injectFallbackData();
          this.isLoading.set(false);
        }, 800);
      }
    });
  }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
    this.fetchLedgerData(event.page, this.rows);
  }

  // =========================================================================
  // DATA EXTRACTION PROTOCOL (CSV EXPORT)
  // =========================================================================
  exportData() {
    const data = this.vehicles();
    if (!data || data.length === 0) {
      this.messageService.add({ severity: 'warn', summary: 'Export Failed', detail: 'No active assets to export.' });
      return;
    }

    // 1. Define the CSV Headers
    const headers = ['Asset ID', 'Make', 'Model', 'Powertrain', 'Base Rate (USD)', 'Current Status'];
    const csvRows = [headers.join(',')]; // Start with the header row

    // 2. Loop through the data and format it
    for (const car of data) {
      const row = [
        car.carId || 'N/A',
        car.brand || 'N/A',
        car.model || 'N/A',
        car.fuelType || 'N/A',
        car.pricePerDay || 0,
        car.status || 'UNKNOWN'
      ];
      // Join columns with commas and add to rows array
      csvRows.push(row.join(','));
    }

    // 3. Create the CSV file in the browser
    const csvContent = csvRows.join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    
    // 4. Trigger the download
    link.setAttribute('href', url);
    link.setAttribute('download', `fleet_ledger_export_${new Date().getTime()}.csv`);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    this.messageService.add({ severity: 'success', summary: 'Export Complete', detail: 'Asset matrix downloaded to terminal.' });
  }

  // Smart fallback that respects the search query!
  injectFallbackData() {
    const allData = [
      { carId: 'V-001', brand: 'Porsche', model: '911 GT3', fuelType: 'PETROL', status: 'AVAILABLE', pricePerDay: 450 },
      { carId: 'V-002', brand: 'Tesla', model: 'Model S Plaid', fuelType: 'ELECTRIC', status: 'BOOKED', pricePerDay: 300 },
      { carId: 'V-003', brand: 'Mercedes', model: 'G63 AMG', fuelType: 'PETROL', status: 'MAINTENANCE', pricePerDay: 500 },
      { carId: 'V-004', brand: 'Audi', model: 'RS e-tron GT', fuelType: 'ELECTRIC', status: 'AVAILABLE', pricePerDay: 280 }
    ];

    const query = this.searchQuery().toLowerCase();
    const filtered = allData.filter(c => 
      c.brand.toLowerCase().includes(query) || 
      c.model.toLowerCase().includes(query) || 
      c.carId.toLowerCase().includes(query)
    );

    this.vehicles.set(filtered);
    this.totalRecords.set(filtered.length);
  }
}