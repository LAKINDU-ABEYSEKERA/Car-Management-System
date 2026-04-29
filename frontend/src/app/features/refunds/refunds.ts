import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { DialogModule } from 'primeng/dialog';
import { MessageService } from 'primeng/api';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-refunds',
  standalone: true,
  imports: [CommonModule, DialogModule, ReactiveFormsModule],
  templateUrl: './refunds.html',
  styleUrl: './refunds.scss'
})
export class RefundsComponent implements OnInit {
  
  private http = inject(HttpClient);
  
  transactions = signal<any[]>([]);
  isLoading = signal(true); 
  isProcessing = signal(false); 

  // NEW: Search Signals
  searchQuery = signal<string>('');
  
  // NEW: The Smart Filter!
  filteredTransactions = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    const allTrx = this.transactions();
    
    if (!query) return allTrx;

    return allTrx.filter(trx => 
      trx.id.toLowerCase().includes(query) || 
      trx.customer.toLowerCase().includes(query)
    );
  });
  
  authModalVisible: boolean = false;
  selectedTrx: any = null;
  refundForm: FormGroup;

  randomHexCodes = ['0xFA99B', '0x11C4D', '0x88F0A', '0xBB772'];
  private hexInterval: any;

  constructor(private fb: FormBuilder, private messageService: MessageService) {
    this.refundForm = this.fb.group({
      reason: ['', [Validators.required, Validators.minLength(10)]],
      adminPin: ['', Validators.required] 
    });
  }

  ngOnInit() {
    this.loadTransactions();
  }

  // NEW: Method to capture keystrokes from the HTML
  updateSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
  }

  loadTransactions() {
    this.isLoading.set(true);
    
    this.http.get<any>(`${environment.apiUrl}/bookings/getAllBookings`).subscribe({
      next: (res) => {
        setTimeout(() => {
          const allBookings = res.data || [];
          
          const mappedLedger = allBookings
            .filter((b: any) => b.bookingStatus === 'CONFIRMED' || b.bookingStatus === 'CANCELLED')
            .map((b: any) => ({
              id: `TRX-${b.bookingId}`,
              bookingId: b.bookingId,  
              customer: b.customerId,
              amount: b.totalPrice + 500, 
              date: b.startDate,
              status: b.bookingStatus === 'CANCELLED' ? 'REFUNDED' : 'SETTLED'
            }));

          mappedLedger.sort((a: any, b: any) => a.status === 'SETTLED' ? -1 : 1);

          this.transactions.set(mappedLedger);
          this.isLoading.set(false);
        }, 800); 
      },
      error: (err) => {
        console.warn('API missing or unreachable. Injecting Secure Fallback Ledger...', err);
        setTimeout(() => {
          this.injectFallbackData();
          this.isLoading.set(false);
        }, 800);
      }
    });
  }

  injectFallbackData() {
    this.transactions.set([
      { id: 'TRX-B001', bookingId: 'B001', customer: 'C001 - Wayne Ent.', amount: 1450.00, date: '2026-04-20', status: 'SETTLED' },
      { id: 'TRX-B002', bookingId: 'B002', customer: 'C084 - Daily Bugle', amount: 890.50, date: '2026-04-21', status: 'SETTLED' },
      { id: 'TRX-B003', bookingId: 'B003', customer: 'C012 - Stark Ind.', amount: 5400.00, date: '2026-04-22', status: 'REFUNDED' }
    ]);
  }

  openRefundAuth(trx: any) {
    if (trx.status === 'REFUNDED') return;
    this.selectedTrx = trx;
    this.refundForm.reset();
    this.authModalVisible = true;
  }

  closeAuth() {
    this.authModalVisible = false;
    this.selectedTrx = null;
    if (this.hexInterval) clearInterval(this.hexInterval);
  }

  executeRefundOverride() {
    if (this.refundForm.invalid) return;
    this.isProcessing.set(true);
    this.randomizeHex();

    const targetBookingId = this.selectedTrx.bookingId;

    this.http.post(`${environment.apiUrl}/payment/refund/${targetBookingId}`, {}).subscribe({
      next: () => {
        setTimeout(() => this.processSuccess(), 2500);
      },
      error: (err) => {
        this.isProcessing.set(false);
        if (this.hexInterval) clearInterval(this.hexInterval);
        
        this.messageService.add({ 
            severity: 'error', 
            summary: 'Override Failed', 
            detail: err.error?.message || 'Database lock prevented decryption.' 
        });
      }
    });
  }

  private randomizeHex() {
    this.hexInterval = setInterval(() => {
      if (this.isProcessing()) {
        this.randomHexCodes = [
          '0x' + Math.floor(Math.random()*16777215).toString(16).toUpperCase().padStart(6, '0'),
          '0x' + Math.floor(Math.random()*16777215).toString(16).toUpperCase().padStart(6, '0'),
          '0x' + Math.floor(Math.random()*16777215).toString(16).toUpperCase().padStart(6, '0')
        ];
      }
    }, 150);
  }

  private processSuccess() {
    this.transactions.update(trxs => 
      trxs.map(t => t.id === this.selectedTrx.id ? { ...t, status: 'REFUNDED' } : t)
    );
    
    this.messageService.add({ 
        severity: 'success', 
        summary: 'Override Successful', 
        detail: `Ledger updated. $${this.selectedTrx.amount} reversed to ${this.selectedTrx.customer}.` 
    });
    
    this.isProcessing.set(false);
    this.closeAuth();
  }
}