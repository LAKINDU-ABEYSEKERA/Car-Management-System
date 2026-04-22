import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class Fleet {
  private http = inject(HttpClient);
  
  vehicles = signal<any[]>([]);
  totalRecords = signal<number>(0); // Required for the UI Arrows!
  
  // The global math variables
  telemetry = signal<any>({ totalFleet: 0, activeHolds: 0, utilizationRate: 0, dailyRevenue: 0 });
  isLoading = signal<boolean>(true);

  // Dynamically accepts limits!
  loadFleet(page: number = 0, size: number = 8) {
    this.isLoading.set(true);
    this.http.get<any>(`${environment.apiUrl}/car/getAllCars?page=${page}&size=${size}`).subscribe({
      next: (response) => {
        this.vehicles.set(response.data.content);
        this.totalRecords.set(response.data.totalElements); // Powers the PrimeNG arrows
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Data uplink failed', err);
        this.isLoading.set(false);
      }
    });
  }

  loadTelemetry() {
    this.http.get<any>(`${environment.apiUrl}/car/telemetry`).subscribe({
      next: (response) => this.telemetry.set(response.data)
    });
  }

  addCar(carPayload: any) {
    return this.http.post<any>(`${environment.apiUrl}/car/addCar`, carPayload);
  }
}