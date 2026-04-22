import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Pricing {
  // Using modern Angular injection
  private http = inject(HttpClient);
  
  // This should point to your Spring Boot server port
  private apiUrl = 'http://localhost:8080/api/v1'; 

  // Endpoint to hit your Spring Boot Pricing Controller
  calculateSurge(vehicleId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/pricing/surge/${vehicleId}`);
  }

  // Endpoint to hit your Spring Boot Seat/Vehicle Hold logic
  lockVehicleHold(vehicleId: string): Observable<any> {
    // Passes the vehicle ID and hardcodes the 10-minute hold rule you engineered
    return this.http.post(`${this.apiUrl}/holds/lock`, { 
        vehicle: vehicleId, 
        durationMinutes: 10 
    });
  }
}