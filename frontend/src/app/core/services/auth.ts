import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private http = inject(HttpClient);
  
  // Signal to hold the decoded user data
  currentUser = signal<any | null>(null);

  authenticate(credentials: any) {
    return this.http.post(`${environment.apiUrl}/auth/login`, credentials).pipe(
      tap((response: any) => {
        // Because your backend wraps the response in StandardResponse, the token is inside the 'data' object
        const token = response.data.token;
        localStorage.setItem('ENTERPRISE_JWT', token);
        this.loadUserFromToken(token);
      })
    );
  }

  // Cracks open the Base64 encoded JWT to extract your email/role
  loadUserFromToken(token: string) {
    try {
      const payload = token.split('.')[1];
      const decodedJson = JSON.parse(atob(payload));
      this.currentUser.set(decodedJson);
    } catch (e) {
      console.error('Failed to decode JWT', e);
      this.currentUser.set(null);
    }
  }

  logout() {
    localStorage.removeItem('ENTERPRISE_JWT');
    this.currentUser.set(null);
  }
}