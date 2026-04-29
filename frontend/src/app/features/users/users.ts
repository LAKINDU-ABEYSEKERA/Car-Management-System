import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { DialogModule } from 'primeng/dialog';
import { MessageService } from 'primeng/api';
import { PaginatorModule } from 'primeng/paginator';
import { environment } from '../../../environments/environment';
import { Auth } from '../../core/services/auth';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, DialogModule, ReactiveFormsModule, PaginatorModule],
  templateUrl: './users.html',
  styleUrl: './users.scss'
})
export class UsersComponent implements OnInit {
  
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private messageService = inject(MessageService);
  private authService = inject(Auth);

  private route = inject(ActivatedRoute);
  private router = inject(Router);

  isAdmin = computed(() => {
    const role = this.authService.currentUser()?.role || 'ADMIN'; 
    return role === 'ADMIN';
  });

  // REAL DATABASE STATE
  users = signal<any[]>([]);
  isLoading = signal(true);
  isSaving = signal(false);

  terminatingUserId = signal<string | null>(null);
  highlightedUserId = signal<string | null>(null); // <-- ADD THIS LINE

  // =========================================================================
  // SEARCH & SMART FILTER ENGINE
  // =========================================================================
  searchQuery = signal<string>('');

  filteredUsers = computed(() => {
    const query = this.searchQuery().toLowerCase().trim();
    const allUsers = this.users();
    
    if (!query) return allUsers;

    return allUsers.filter(u => 
      (u.userName || '').toLowerCase().includes(query) || 
      (u.email || '').toLowerCase().includes(query)
    );
  });

  // =========================================================================
  // PAGINATION ENGINE
  // =========================================================================
  first = signal<number>(0);
  rows = signal<number>(6); 
  
  // FIX 1: Count the filtered users, not the raw users!
  totalRecords = computed(() => this.filteredUsers().length);

  // FIX 2: Slice the filtered array!
  paginatedUsers = computed(() => {
    const start = this.first();
    const end = start + this.rows();
    return this.filteredUsers().slice(start, end);
  });

  modalVisible = false;
  userForm: FormGroup;
  selectedUserId: string | null = null; 

  constructor() {
    this.userForm = this.fb.group({
      userName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: [''], 
      role: ['USER', Validators.required] // FIX 3: Changed STAFF to USER to match backend!
    });
  }

  ngOnInit() {
    this.loadUsers();
  }

  // Captures keystrokes and resets pagination
  updateSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchQuery.set(input.value);
    this.first.set(0); 
  }

  loadUsers() {
    this.isLoading.set(true);
    
    this.http.get<any>(`${environment.apiUrl}/user/getAllUsers`).subscribe({
      next: (res) => {
        // 1. ACTUALLY SAVE THE DATA TO THE SIGNAL
        this.users.set(res.data || []);
        
        // 2. TURN OFF THE LOADING SKELETONS
        this.isLoading.set(false);

        // 3. AUTO-EDIT INTERCEPTOR LOGIC
        setTimeout(() => {
          const targetEmail = this.route.snapshot.queryParams['autoEdit'];
          
          if (targetEmail) {
            // Search the loaded database records for your exact email
            const myProfile = this.users().find(u => u.email === targetEmail || u.userName === targetEmail);
            
            if (myProfile) {
              // 1. Trigger the Matrix Glow effect on the specific card
              this.highlightedUserId.set(myProfile.userId);
              
              // 2. Wait 1.2 seconds for you to see the glow, THEN open the modal
              setTimeout(() => {
                this.openUserModal(myProfile);
                
                // Clear the glow signal so it can trigger again later
                setTimeout(() => this.highlightedUserId.set(null), 500); 
              }, 1200); 
              
              // Scrub the URL clean so it doesn't keep opening if you hit refresh
              this.router.navigate([], { queryParams: { autoEdit: null }, queryParamsHandling: 'merge' });
            }
          }
        }, 300); // Small 300ms delay ensures the DOM has rendered the table first
      },
      error: (err) => {
        console.error('CRITICAL: Failed to retrieve personnel from DB.', err);
        this.users.set([]);
        this.isLoading.set(false);
        this.messageService.add({ severity: 'error', summary: 'Connection Refused', detail: 'Could not reach Identity Database.' });
      }
    });
  }

  onPageChange(event: any) {
    this.first.set(event.first);
    this.rows.set(event.rows);
  }

  openUserModal(user?: any) {
    this.userForm.reset();
    if (user) {
      this.selectedUserId = user.userId;
      this.userForm.patchValue({
        userName: user.userName,
        email: user.email,
        role: user.role
      });
      this.userForm.get('password')?.clearValidators();
    } else {
      this.selectedUserId = null;
      this.userForm.patchValue({ role: 'USER' }); 
      this.userForm.get('password')?.setValidators([Validators.required, Validators.minLength(6)]);
    }
    this.userForm.get('password')?.updateValueAndValidity();
    this.modalVisible = true;
  }

  saveUser() {
    if (this.userForm.invalid) return;
    this.isSaving.set(true);
    
    const payload = this.userForm.value;
    
    const request = this.selectedUserId 
      ? this.http.put(`${environment.apiUrl}/user/updateUser/${this.selectedUserId}`, payload)
      : this.http.post(`${environment.apiUrl}/auth/register`, payload); 

    request.subscribe({
      next: (res: any) => {
        setTimeout(() => { 
          this.messageService.add({ severity: 'success', summary: 'System Updated', detail: res.message || 'Personnel file synchronized.' });
          this.modalVisible = false;
          this.isSaving.set(false);
          this.loadUsers(); 
        }, 1000);
      },
      error: (err) => {
        this.messageService.add({ severity: 'error', summary: 'Synchronization Failed', detail: err.error?.message || 'Database connection refused.' });
        this.isSaving.set(false);
      }
    });
  }

  terminateUser(id: string) {
    if (!this.isAdmin()) return;

    this.terminatingUserId.set(id);

    setTimeout(() => {
      this.http.delete(`${environment.apiUrl}/user/deleteUser/${id}`).subscribe({
        next: (res: any) => {
          this.users.update(users => users.filter(u => u.userId !== id));
          this.terminatingUserId.set(null);
          
          if (this.paginatedUsers().length === 0 && this.first() > 0) {
            this.first.set(Math.max(0, this.first() - this.rows()));
          }

          this.messageService.add({ severity: 'warn', summary: 'Profile Terminated', detail: res.message || 'User access permanently revoked.' });
        },
        error: (err) => {
          this.terminatingUserId.set(null);
          this.messageService.add({ severity: 'error', summary: 'Termination Failed', detail: err.error?.message || 'System block encountered.' });
        }
      });
    }, 600); 
  }
}