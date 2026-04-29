import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule], 
  templateUrl: './sidebar.html'
})
export class Sidebar {
  authService = inject(Auth);

  // Dynamically checks your auth state. Defaulting to true for development!
  isAdmin = computed(() => {
    const role = this.authService.currentUser()?.role || 'ADMIN';
    return role === 'ADMIN';
  });

  // DATA-DRIVEN ARCHITECTURE: Easy to maintain and scale!
  menuItems = [
    { label: 'Command Center', icon: 'pi-th-large', route: '/dashboard', requiresAdmin: false },
    { label: 'Fleet Ledger', icon: 'pi-car', route: '/fleet', requiresAdmin: false },
    { label: 'Client Registry', icon: 'pi-users', route: '/customers', requiresAdmin: false },
    { label: 'Driver Dispatch', icon: 'pi-id-card', route: '/drivers', requiresAdmin: false },
    
    // LEVEL 5 CLEARANCE ONLY
    { label: 'Financial Refunds', icon: 'pi-credit-card', route: '/refunds', requiresAdmin: true },
    { label: 'User Control Panel', icon: 'pi-shield', route: '/users', requiresAdmin: true },
    { label: 'System Audit Logs', icon: 'pi-database', route: '/audit', requiresAdmin: true }
  ];
}