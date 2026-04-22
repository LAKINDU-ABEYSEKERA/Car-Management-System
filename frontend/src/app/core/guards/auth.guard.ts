import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  
  // Check if our secure vault has a token
  const token = localStorage.getItem('ENTERPRISE_JWT');

  if (token) {
    return true; // Access Granted
  } else {
    // Access Denied: Kick them back to the login screen
    router.navigate(['/login']);
    return false;
  }
};