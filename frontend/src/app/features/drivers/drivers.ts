import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { DialogModule } from 'primeng/dialog';
import { MessageService } from 'primeng/api';
import { PaginatorModule } from 'primeng/paginator';
import { environment } from '../../../environments/environment';
import { Auth } from '../../core/services/auth';

@Component({
  selector: 'app-drivers',
  standalone: true,
  imports: [CommonModule, DialogModule, ReactiveFormsModule, PaginatorModule],
  templateUrl: './drivers.HTML',
  styleUrl: './drivers.scss'
})
export class Drivers implements OnInit {
  
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private messageService = inject(MessageService);
  private authService = inject(Auth);

  isAdmin = computed(() => {
    const role = this.authService.currentUser()?.role || 'ADMIN'; 
    return role === 'ADMIN';
  });

  drivers = signal<any[]>([]);
  isLoading = signal(true);
  isSaving = signal(false);
  
  // Animation Triggers
  terminatingId = signal<string | null>(null);
  recentlyUpdatedId = signal<string | null>(null);

  // CLIENT-SIDE SEARCH ENGINE
  searchQuery = signal<string>('');
  filteredDrivers = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    const all = this.drivers();
    if (!query) return all;
    return all.filter(d => 
      (d.name || '').toLowerCase().includes(query) || 
      (d.licenceNo || '').toLowerCase().includes(query) ||
      (d.driverId || '').toLowerCase().includes(query)
    );
  });

  // CLIENT-SIDE PAGINATION ENGINE
  first = signal<number>(0);
  rows = signal<number>(6); 
  totalRecords = computed(() => this.filteredDrivers().length);

  paginatedDrivers = computed(() => {
    const start = this.first();
    const end = start + this.rows();
    return this.filteredDrivers().slice(start, end);
  });

  modalVisible = false;
  driverForm: FormGroup;
  selectedId: string | null = null; 

  constructor() {
    this.driverForm = this.fb.group({
      name: ['', Validators.required],
      licenceNo: ['', [Validators.required, Validators.pattern('^[A-Z0-9-]+$')]],
      status: ['AVAILABLE', Validators.required]
    });
  }

  ngOnInit() {
    this.loadDrivers();
  }

  updateSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
    this.first.set(0); // Snap back to page 1 on search
  }

  loadDrivers() {
    this.isLoading.set(true);
    
    this.http.get<any>(`${environment.apiUrl}/driver/getAllDrivers`).subscribe({
      next: (res) => {
        setTimeout(() => {
          // Unpack the StandardResponse
          this.drivers.set(res.data || []);
          this.first.set(0);
          this.isLoading.set(false);
        }, 500); 
      },
      error: (err) => {
        console.error('API Error', err);
        this.drivers.set([]);
        this.isLoading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Connection Refused', detail: 'Could not reach Dispatch Server.' });
      }
    });
  }

  onPageChange(event: any) {
    this.first.set(event.first);
    this.rows.set(event.rows);
  }

  openModal(driver?: any) {
    this.driverForm.reset();
    if (driver) {
      this.selectedId = driver.driverId;
      this.driverForm.patchValue(driver);
    } else {
      this.selectedId = null;
      this.driverForm.patchValue({ status: 'AVAILABLE' });
    }
    this.modalVisible = true;
  }

  saveDriver() {
    if (this.driverForm.invalid) return;
    this.isSaving.set(true);
    
    const payload = this.driverForm.value;
    const isUpdate = !!this.selectedId;
    
    const request = isUpdate 
      ? this.http.put(`${environment.apiUrl}/driver/updateDriver/${this.selectedId}`, payload)
      : this.http.post(`${environment.apiUrl}/driver/addDriver`, payload); 

    request.subscribe({
      next: () => {
        setTimeout(() => { 
          this.messageService.add({ severity: 'success', summary: 'Dispatch Updated', detail: 'Driver profile synchronized.' });
          this.modalVisible = false;
          this.isSaving.set(false);
          this.loadDrivers(); 

          if (isUpdate) {
            this.recentlyUpdatedId.set(this.selectedId);
            setTimeout(() => this.recentlyUpdatedId.set(null), 1500);
          }
        }, 1000); 
      },
      error: (err) => {
        this.messageService.add({ severity: 'error', summary: 'Sync Failed', detail: err.error?.message || 'Transaction rejected.' });
        this.isSaving.set(false);
      }
    });
  }

  deleteDriver(id: string) {
    if (!this.isAdmin()) return;

    this.terminatingId.set(id);

    setTimeout(() => {
      this.http.delete(`${environment.apiUrl}/driver/deleteDriver/${id}`).subscribe({
        next: () => {
          this.terminatingId.set(null);
          this.messageService.add({ severity: 'warn', summary: 'Driver Expunged', detail: 'Record removed from dispatch.' });
          
          // Fast UI update without reloading from server
          this.drivers.update(list => list.filter(d => d.driverId !== id));
          
          if (this.paginatedDrivers().length === 0 && this.first() > 0) {
            this.first.set(Math.max(0, this.first() - this.rows()));
          }
        },
        error: (err) => {
          this.terminatingId.set(null);
          this.messageService.add({ severity: 'error', summary: 'Purge Failed', detail: err.error?.message || 'Database lock.' });
        }
      });
    }, 800); // Matches the CSS evaporation time
  }

  // Helper for UI styling
  getStatusColor(status: string): string {
    switch (status) {
      case 'AVAILABLE': return 'text-emerald-400 bg-emerald-400/10 border-emerald-400/30';
      case 'ON_TRIP': return 'text-blue-400 bg-blue-400/10 border-blue-400/30';
      case 'MAINTENANCE': return 'text-rose-400 bg-rose-400/10 border-rose-400/30';
      default: return 'text-amber-400 bg-amber-400/10 border-amber-400/30';
    }
  }
}