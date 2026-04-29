import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { DialogModule } from 'primeng/dialog';
import { MessageService } from 'primeng/api';
import { PaginatorModule } from 'primeng/paginator';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, DialogModule, ReactiveFormsModule, PaginatorModule],
  templateUrl: './customers.html',
  styleUrl: './customers.scss'
})
export class Customers implements OnInit {
  
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private messageService = inject(MessageService);

  customers = signal<any[]>([]);
  isLoading = signal(true);
  isSaving = signal(false);
  
  // Animation Triggers
  terminatingId = signal<string | null>(null);
  recentlyUpdatedId = signal<string | null>(null);

  currentPage = signal<number>(0);
  pageSize = signal<number>(6); 
  totalRecords = signal<number>(0);

  // SEARCH ENGINE WITH DEBOUNCE
  searchQuery = signal<string>('');
  private searchTimeout: any;

  modalVisible = false;
  customerForm: FormGroup;
  selectedId: string | null = null; 

  constructor() {
    this.customerForm = this.fb.group({
      customerName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      address: ['', Validators.required]
    });
  }

  ngOnInit() {
    this.loadCustomers(0);
  }

  // Waits 400ms after the user stops typing before hitting the Database!
  updateSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    if (this.searchTimeout) clearTimeout(this.searchTimeout);
    
    this.searchTimeout = setTimeout(() => {
      this.searchQuery.set(input.value);
      this.currentPage.set(0); 
      this.loadCustomers(0); // Trigger search
    }, 400);
  }

  loadCustomers(page: number) {
    this.isLoading.set(true);
    const query = encodeURIComponent(this.searchQuery().trim());
    const url = `${environment.apiUrl}/customers/getAllCustomers?page=${page}&size=${this.pageSize()}&search=${query}`;

    this.http.get<any>(url).subscribe({
      next: (res) => {
        setTimeout(() => {
          const paginatedData = res.data;
          this.customers.set(paginatedData.content || []);
          this.totalRecords.set(paginatedData.totalElements || 0);
          this.currentPage.set(paginatedData.pageNumber || 0);
          this.isLoading.set(false);
        }, 300); 
      },
      error: (err) => {
        this.isLoading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Connection Error', detail: 'Registry unreachable.' });
      }
    });
  }

  onPageChange(event: any) {
    this.pageSize.set(event.rows);
    this.loadCustomers(event.page);
  }

  openModal(customer?: any) {
    this.customerForm.reset();
    if (customer) {
      this.selectedId = customer.customerId;
      this.customerForm.patchValue(customer);
    } else {
      this.selectedId = null;
    }
    this.modalVisible = true;
  }

  saveCustomer() {
    if (this.customerForm.invalid) return;
    this.isSaving.set(true);
    
    const payload = this.customerForm.value;
    const isUpdate = !!this.selectedId;
    const request = isUpdate
      ? this.http.put(`${environment.apiUrl}/customers/updateCustomer/${this.selectedId}`, payload)
      : this.http.post(`${environment.apiUrl}/customers/addCustomer`, payload); 

    request.subscribe({
      next: () => {
        setTimeout(() => { 
          this.messageService.add({ severity: 'success', summary: 'Registry Updated', detail: 'Client data assembled.' });
          this.modalVisible = false;
          this.isSaving.set(false);
          this.loadCustomers(this.currentPage()); 

          // Trigger the Emerald Flash Animation if it was an update!
          if (isUpdate) {
            this.recentlyUpdatedId.set(this.selectedId);
            setTimeout(() => this.recentlyUpdatedId.set(null), 1500);
          }

        }, 1200); 
      },
      error: () => this.isSaving.set(false)
    });
  }

  deleteCustomer(id: string) {
    // Triggers Phase 1: The Red Target Lock
    this.terminatingId.set(id);

    // Wait 1.2s for the full Lock & Shred animation to finish before hitting the API
    setTimeout(() => {
      this.http.delete(`${environment.apiUrl}/customers/deleteCustomer/${id}`).subscribe({
        next: () => {
          this.terminatingId.set(null);
          this.messageService.add({ severity: 'warn', summary: 'Client Purged', detail: 'Record expunged from database.' });
          
          const isLastItemOnPage = this.customers().length === 1;
          const targetPage = (isLastItemOnPage && this.currentPage() > 0) ? this.currentPage() - 1 : this.currentPage();
          this.loadCustomers(targetPage);
        },
        error: () => this.terminatingId.set(null)
      });
    }, 1200); 
  }
}