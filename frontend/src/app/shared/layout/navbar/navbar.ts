import { Component, inject, OnInit, ElementRef, HostListener, ViewChild, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router'; 
import { HttpClient } from '@angular/common/http';
import { Auth } from '../../../core/services/auth';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html'
})
export class Navbar implements OnInit {
  
  authService = inject(Auth);
  router = inject(Router); 
  private http = inject(HttpClient);
  
  @ViewChild('searchInput') searchInput!: ElementRef;

  isProfileOpen = signal(false);
  isNotifOpen = signal(false);
  
  // Real-time System Alerts
  notifications = signal<any[]>([]);
  unreadCount = computed(() => this.notifications().length);

  // Dynamic Admin Check
  isAdmin = computed(() => {
    const role = this.authService.currentUser()?.role || 'ADMIN'; 
    return role === 'ADMIN';
  });

  ngOnInit() {
    const token = localStorage.getItem('ENTERPRISE_JWT');
    if (token) {
      this.authService.loadUserFromToken(token);
    }
    // Ping the server for active alerts on boot
    this.fetchSystemAlerts();
  }

  // =========================================================================
  // TELEMETRY ALERTS (DYNAMIC NOTIFICATIONS)
  // =========================================================================
  fetchSystemAlerts() {
    // We fetch the ledger to find assets currently BOOKED/Deployed
    this.http.get<any>(`${environment.apiUrl}/car/getAllCars?size=50`).subscribe({
      next: (res) => {
        const cars = res.data?.content || [];
        
        // Filter for deployed assets to simulate our "Attention Needed / Overdue" alerts
        const activeAlerts = cars
          .filter((c: any) => c.status === 'BOOKED')
          .slice(0, 4); // Keep the dropdown clean with max 4 alerts

        const formattedAlerts = activeAlerts.map((c: any) => ({
          id: c.carId,
          title: 'Asset Deployed / Active',
          message: `${c.brand} ${c.model} (${c.carId}) requires tracking.`,
          time: 'Live',
          type: 'WARN'
        }));

        this.notifications.set(formattedAlerts);
      },
      error: (err) => console.warn("Failed to fetch telemetry for notifications", err)
    });
  }

  clearAlerts() {
    this.notifications.set([]);
  }

  // =========================================================================
  // ROUTING & MENU CONTROLS
  // =========================================================================
  toggleProfile() {
    this.isProfileOpen.update(v => !v);
    this.isNotifOpen.set(false);
  }

  toggleNotif() {
    this.isNotifOpen.update(v => !v);
    this.isProfileOpen.set(false);
  }

  closeMenus() {
    this.isProfileOpen.set(false);
    this.isNotifOpen.set(false);
  }

  goToUsers() {
    this.closeMenus();
    this.router.navigate(['/users']);
  }


  editMyProfile() {
    this.closeMenus();
    // Grab the current user's email/identifier from the JWT token
    const userIdentifier = this.authService.currentUser()?.sub; 
    
    // Route to Identity & Access, passing the identifier as a secure query parameter
    this.router.navigate(['/users'], { queryParams: { autoEdit: userIdentifier } });
  }

  goToLogs() {
    this.closeMenus();
    this.router.navigate(['/audit']);
  }

  logout() {
    console.log("Terminating Session...");
    this.closeMenus();
    localStorage.removeItem('ENTERPRISE_JWT'); 
    this.router.navigate(['/login']);
  }

  // =========================================================================
  // SEARCH & INTERACTION LOGIC
  // =========================================================================
  executeSearch(query: string) {
    if (!query || query.trim() === '') {
        this.router.navigate(['/dashboard'], { queryParams: { search: null } });
        return;
    }
    this.router.navigate(['/dashboard'], { queryParams: { search: query.trim() } });
    this.searchInput.nativeElement.blur();
  }

  triggerNotification(event: MouseEvent, carId: string) {
    const element = event.currentTarget as HTMLElement;
    
    // Fires the purple 'animate-cyber-warp' effect!
    element.classList.add('animate-cyber-warp');
    
    setTimeout(() => {
      this.closeMenus();
      this.router.navigate(['/dashboard'], { queryParams: { focus: carId } });
      element.classList.remove('animate-cyber-warp');
    }, 400);
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.key === '/' && document.activeElement !== this.searchInput?.nativeElement) {
      event.preventDefault(); 
      this.searchInput?.nativeElement.focus();
    }
    if (event.key === 'Escape') this.closeMenus();
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    const target = event.target as HTMLElement;
    if (!target.closest('.navbar-interactive')) this.closeMenus();
  }
}