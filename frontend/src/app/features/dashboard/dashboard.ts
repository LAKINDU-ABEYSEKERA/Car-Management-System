import { Component, inject, OnInit, OnDestroy, ChangeDetectorRef, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogModule } from 'primeng/dialog';
import { PaginatorModule } from 'primeng/paginator'; 
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http'; 
import { environment } from '../../../environments/environment'; 
import { Checkout } from '../../core/services/checkout';
import { Fleet } from '../../core/services/fleet';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DialogModule, ReactiveFormsModule, PaginatorModule],
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit, OnDestroy {
  checkoutModalVisible: boolean = false;
  addCarModalVisible: boolean = false;
  returnModalVisible: boolean = false; 
  
  isProcessing: boolean = false;
  isAddingCar: boolean = false;
  isReturning: boolean = false;
  selectedCar: any = null;

  carForm: FormGroup; 
  checkoutForm: FormGroup;
  returnForm: FormGroup; 

  dynamicTotal: number = 0;
  daysRequested: number = 1;
  readonly DRIVER_FEE_PER_DAY = 50; 
  readonly SECURITY_DEPOSIT = 500;

  holdTimeLeft: number = 600; 
  displayTime: string = '10:00';
  timerInterval: any;

  private http = inject(HttpClient);
  private checkoutService = inject(Checkout);
  private messageService = inject(MessageService);
  fleetService = inject(Fleet);
  private fb = inject(FormBuilder);
  private cdr = inject(ChangeDetectorRef);

  first: number = 0;
  rows: number = 8; 

  activeBookingsMap = new Map<string, string>(); 

  // =========================================================================
  // THE LIVE TELEMETRY & SURGE ENGINE
  // =========================================================================
  totalFleet = computed(() => this.fleetService.vehicles().length);
  activeHolds = computed(() => this.fleetService.vehicles().filter(v => v.status === 'BOOKED').length);
  
  utilizationRate = computed(() => {
    const total = this.totalFleet();
    return total === 0 ? 0 : Math.round((this.activeHolds() / total) * 100);
  });

  // NEW: The Dynamic Surge Multiplier
  surgeMultiplier = computed(() => {
    const util = this.utilizationRate();
    if (util >= 85) return 1.25; // CRITICAL SURGE: +25%
    if (util >= 70) return 1.15; // ELEVATED SURGE: +15%
    return 1.0;                  // STANDARD
  });

  // NEW: UI Helper for the Telemetry Card
  surgeLabel = computed(() => {
    const util = this.utilizationRate();
    if (util >= 85) return 'Critical Demand Surge';
    if (util >= 70) return 'Elevated Demand Surge';
    return 'Standard Market Rates';
  });

  dailyRevenue = computed(() => {
    return this.fleetService.vehicles()
      .filter(v => v.status === 'BOOKED')
      .reduce((sum, car) => sum + car.pricePerDay, 0);
  });

  loadActiveBookings() {
    this.http.get<any>(`${environment.apiUrl}/bookings/getAllBookings`).subscribe({
      next: (res) => {
        this.activeBookingsMap.clear();
        const bookings = res.data || [];
        bookings.forEach((b: any) => {
          if (b.bookingStatus === 'CONFIRMED') {
            this.activeBookingsMap.set(b.carId, b.endDate);
          }
        });
      }
    });
  }

  getBookingUrgency(carId: string): 'SAFE' | 'DUE_TODAY' | 'OVERDUE' {
    const endDateStr = this.activeBookingsMap.get(carId);
    if (!endDateStr) return 'SAFE'; 

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const endDate = new Date(endDateStr);
    endDate.setHours(0, 0, 0, 0);

    const diffDays = Math.round((endDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

    if (diffDays < 0) return 'OVERDUE';
    if (diffDays === 0) return 'DUE_TODAY';
    return 'SAFE';
  }

  constructor() {
    this.carForm = this.fb.group({
      brand: ['', Validators.required], model: ['', Validators.required],
      seatingCapacity: [4, [Validators.required, Validators.min(2), Validators.max(15)]],
      fuelType: ['ELECTRIC', Validators.required], pricePerDay: [150, [Validators.required, Validators.min(1)]],
      status: ['AVAILABLE']
    });
    this.checkoutForm = this.fb.group({
      startDate: ['', Validators.required], endDate: ['', Validators.required], withDriver: [false]
    });
    this.returnForm = this.fb.group({
      lateFees: [0, [Validators.required, Validators.min(0)]], damageFees: [0, [Validators.required, Validators.min(0)]]
    });
    this.checkoutForm.valueChanges.subscribe(() => this.calculateDynamicPricing());
  }

  ngOnInit() { 
    this.fleetService.loadFleet(0, this.rows); 
    this.fleetService.loadTelemetry(); 
    this.loadActiveBookings(); 
  }
  
  ngOnDestroy() { this.stopTimer(); }

  onPageChange(event: any) {
    this.first = event.first;
    this.rows = event.rows;
    this.fleetService.loadFleet(event.page, this.rows);
  }

  refreshDashboard() {
    const currentPage = Math.floor(this.first / this.rows);
    this.fleetService.loadFleet(currentPage, this.rows);
    this.fleetService.loadTelemetry();
    this.loadActiveBookings(); 
  }

  openAddCarModal() {
    this.carForm.reset({ fuelType: 'ELECTRIC', seatingCapacity: 4, pricePerDay: 150, status: 'AVAILABLE' });
    this.addCarModalVisible = true;
  }

  submitNewCar() {
    if (this.carForm.valid) {
      this.isAddingCar = true;
      this.fleetService.addCar(this.carForm.value).subscribe({
        next: () => {
          this.isAddingCar = false;
          this.addCarModalVisible = false;
          this.messageService.add({ severity: 'success', summary: 'Asset Registered', detail: 'Deployed to fleet.' });
          this.refreshDashboard();
        },
        error: (err) => {
          this.isAddingCar = false;
          this.messageService.add({ severity: 'error', summary: 'Failed', detail: err.error?.message });
        }
      });
    } else { this.carForm.markAllAsTouched(); }
  }

  handleCarClick(car: any) {
    this.selectedCar = car;
    if (car.status === 'AVAILABLE') {
      const today = new Date();
      const tomorrow = new Date(today);
      tomorrow.setDate(tomorrow.getDate() + 1);
      this.checkoutForm.patchValue({
        startDate: today.toISOString().split('T')[0], endDate: tomorrow.toISOString().split('T')[0], withDriver: false
      });
      this.calculateDynamicPricing();
      this.checkoutModalVisible = true;
      this.startCheckoutTimer();
    } else if (car.status === 'BOOKED') {
      this.returnForm.reset({ lateFees: 0, damageFees: 0 });
      this.returnModalVisible = true;
    }
  }

  executeReturn() {
    if (!this.selectedCar || this.returnForm.invalid) return;
    this.isReturning = true;
    const fees = this.returnForm.value;
    this.checkoutService.processVehicleReturn(this.selectedCar.carId, fees.lateFees, fees.damageFees).subscribe({
      next: () => {
        this.isReturning = false;
        this.returnModalVisible = false;
        this.messageService.add({ severity: 'success', summary: 'Capture Complete', detail: `${this.selectedCar.brand} returned.` });
        this.refreshDashboard(); 
      },
      error: (err) => {
        this.isReturning = false;
        this.messageService.add({ severity: 'error', summary: 'Failed', detail: err.error?.message });
      }
    });
  }

  // UPGRADED: Factors the Surge Multiplier into the final payload math!
  calculateDynamicPricing() {
    if (!this.selectedCar) return;
    const start = new Date(this.checkoutForm.value.startDate);
    const end = new Date(this.checkoutForm.value.endDate);
    let diffDays = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24));
    if (diffDays <= 0 || isNaN(diffDays)) diffDays = 1;
    this.daysRequested = diffDays;
    
    // Apply Surge Math to Base Rate
    let baseSurgedRate = this.selectedCar.pricePerDay * this.surgeMultiplier();
    let calculatedTotal = baseSurgedRate * this.daysRequested;
    
    // Add Chauffeur Fee (Standard rate, untouched by surge)
    if (this.checkoutForm.value.withDriver) calculatedTotal += (this.DRIVER_FEE_PER_DAY * this.daysRequested);
    
    this.dynamicTotal = calculatedTotal;
  }

  executeTransaction() {
    if (!this.selectedCar || this.checkoutForm.invalid) return;
    this.isProcessing = true;
    const formVals = this.checkoutForm.value;
    this.checkoutService.processVehicleHold(
      this.selectedCar.carId, 'C001', formVals.startDate, formVals.endDate, this.dynamicTotal, formVals.withDriver
    ).subscribe({
      next: () => {
        this.isProcessing = false;
        this.closeCheckoutModal(); 
        this.messageService.add({ severity: 'success', summary: 'Asset Secured', detail: `${this.selectedCar.brand} is locked.` });
        this.refreshDashboard(); 
      },
      error: (err) => {
        this.isProcessing = false;
        this.messageService.add({ severity: 'error', summary: 'Failed', detail: err.error?.message });
      }
    });
  }

  startCheckoutTimer() {
    this.stopTimer(); 
    this.holdTimeLeft = 600; 
    this.updateDisplayTime();
    this.timerInterval = setInterval(() => {
      this.holdTimeLeft--;
      this.updateDisplayTime();
      this.cdr.detectChanges(); 
      if (this.holdTimeLeft <= 0) {
        this.closeCheckoutModal();
        this.messageService.add({ severity: 'warn', summary: 'Session Expired', detail: 'Reservation window timed out.' });
      }
    }, 1000);
  }

  updateDisplayTime() {
    const minutes = Math.floor(this.holdTimeLeft / 60);
    const seconds = this.holdTimeLeft % 60;
    this.displayTime = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  }
  
  stopTimer() { if (this.timerInterval) clearInterval(this.timerInterval); }
  closeCheckoutModal() { this.checkoutModalVisible = false; this.stopTimer(); }
}