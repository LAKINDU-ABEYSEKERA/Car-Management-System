import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../../core/services/auth';

// Import the Message Service to trigger the UI notification
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html'
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(Auth);
  private router = inject(Router);
  // Inject the service
  private messageService = inject(MessageService);

  loginForm: FormGroup = this.fb.group({
    operatorId: ['', [Validators.required, Validators.email]],
    clearanceCode: ['', [Validators.required, Validators.minLength(6)]]
  });

  isAuthenticating = false;

  submitCredentials() {
    if (this.loginForm.valid) {
      this.isAuthenticating = true;
      
      // THE FIX: Translate the futuristic UI terms into standard backend DTO terms
      const backendPayload = {
        email: this.loginForm.value.operatorId,
        password: this.loginForm.value.clearanceCode
      };
      
      // Send the translated payload to Spring Boot
      this.authService.authenticate(backendPayload).subscribe({
        next: (response) => {
          // Success: The interceptor locked the JWT in the vault, let them in!
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          this.isAuthenticating = false;
          
          this.messageService.add({
            severity: 'error', 
            summary: 'Authorization Denied', 
            detail: 'Spring Boot rejected the credentials. Please verify your clearance code.',
            life: 5000 
          });
          
          console.error('System Authorization Failed', err);
        }
      });
    } else {
      this.loginForm.markAllAsTouched();
      this.messageService.add({
        severity: 'warn', 
        summary: 'Incomplete Data', 
        detail: 'Operator ID and Clearance Code are required for system entry.'
      });
    }
  }
}