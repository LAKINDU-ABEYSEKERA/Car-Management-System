import { Routes } from '@angular/router';
// Import your new Bouncer
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  // 1. Default route sends you to login
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  
  // 2. The Public Zone (Unprotected)
  { 
    path: 'login', 
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login) 
  },
  
  // 3. The Secure Zone (Protected by authGuard)
  { 
    path: 'dashboard', 
    canActivate: [authGuard], 
    loadComponent: () => import('./features/dashboard/dashboard').then(m => m.Dashboard) 
  },
  { 
    path: 'fleet', 
    canActivate: [authGuard], 
    loadComponent: () => import('./features/fleet-ledger/fleet-ledger').then(m => m.FleetLedger) 
  },
  
  // 4. Catch-all kicks intruders back to login
  { path: '**', redirectTo: 'login' }
];