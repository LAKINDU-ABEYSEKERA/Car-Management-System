import { Component, inject } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs';
import { CommonModule } from '@angular/common';

import { Navbar } from './shared/layout/navbar/navbar';
import { Sidebar } from './shared/layout/sidebar/sidebar';
// Import the Toast Module
import { ToastModule } from 'primeng/toast';

@Component({
  selector: 'app-root',
  standalone: true,
  // Add ToastModule to your imports
  imports: [RouterOutlet, Navbar, Sidebar, CommonModule, ToastModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent {
  title = 'Enterprise Rentals';
  isAuthRoute = false;
  private router = inject(Router);

  constructor() {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.isAuthRoute = event.urlAfterRedirects.includes('/login');
    });
  }
}