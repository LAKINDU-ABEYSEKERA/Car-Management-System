import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { switchMap, map } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Checkout {
  private http = inject(HttpClient);

  // Phase 1: The Hold
  processVehicleHold(carId: string, customerId: string, startDate: string, endDate: string, totalPrice: number, withDriver: boolean) {
    const bookingPayload = {
      carId: carId, customerId: customerId, startDate: startDate,
      endDate: endDate, totalPrice: totalPrice, withDriver: withDriver
    };

    return this.http.post<any>(`${environment.apiUrl}/bookings/createBooking`, bookingPayload).pipe(
      switchMap((bookingResponse) => {
        const paymentPayload = {
          bookingId: bookingResponse.data.bookingId,
          paymentMethod: 'CREDIT_CARD' 
        };
        return this.http.post<any>(`${environment.apiUrl}/payment/authorize`, paymentPayload);
      })
    );
  }

  // Phase 2: The Return & Capture
  processVehicleReturn(carId: string, lateFees: number, damageFees: number) {
    // 1. Scan all bookings to find the active one for this specific car
    return this.http.get<any>(`${environment.apiUrl}/bookings/getAllBookings`).pipe(
      switchMap((response) => {
        const bookings = response.data;
        // Find the specific booking where this car is currently CONFIRMED
        const activeBooking = bookings.find((b: any) => b.carId === carId && b.bookingStatus === 'CONFIRMED');
        
        if (!activeBooking) {
          throw new Error('Could not locate an active confirmed booking for this asset.');
        }

        // 2. Execute the financial capture and release the car
        return this.http.put<any>(
          `${environment.apiUrl}/bookings/returnCar/${activeBooking.bookingId}?lateFees=${lateFees}&damageFees=${damageFees}`, 
          {}
        );
      })
    );
  }
}