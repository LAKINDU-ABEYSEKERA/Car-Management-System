import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html'
})
export class Navbar implements OnInit {
  authService = inject(Auth);

  ngOnInit() {
    // If the user refreshes the page, reload their identity from the saved token
    const token = localStorage.getItem('ENTERPRISE_JWT');
    if (token) {
      this.authService.loadUserFromToken(token);
    }
  }
}