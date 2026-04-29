import { Component, OnInit, OnDestroy, signal, ViewChild, ElementRef, AfterViewChecked, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

interface LogEntry {
  eventId: string;
  timestamp: string;
  logLevel: 'INFO' | 'WARN' | 'CRITICAL' | 'REFUND';
  service: string;
  message: string;
  username: string;
}

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit.html',
  styleUrl: './audit.scss'
})
export class Audit implements OnInit, OnDestroy, AfterViewChecked {
  
  private http = inject(HttpClient);
  @ViewChild('terminalBody') private terminalBody!: ElementRef;

  logs = signal<LogEntry[]>([]);
  isStreaming = signal(true);
  private streamInterval: any;
  private previousLogCount = 0; // Tracks if we need to auto-scroll

  ngOnInit() {
    this.fetchLiveLogs();
    this.startLiveSimulation(); // Starts the 2-second polling
  }

  ngOnDestroy() {
    if (this.streamInterval) clearInterval(this.streamInterval);
  }

  ngAfterViewChecked() {
    // Only auto-scroll if new logs actually arrived
    if (this.isStreaming() && this.logs().length > this.previousLogCount) {
      this.scrollToBottom();
      this.previousLogCount = this.logs().length;
    }
  }

  scrollToBottom(): void {
    try {
      this.terminalBody.nativeElement.scrollTop = this.terminalBody.nativeElement.scrollHeight;
    } catch(err) { }
  }

  toggleStream() {
    this.isStreaming.update(v => !v);
    if (this.isStreaming()) {
      this.startLiveSimulation();
    } else {
      clearInterval(this.streamInterval);
    }
  }

  // =========================================================================
  // LIVE MYSQL CONNECTIONS
  // =========================================================================
  startLiveSimulation() {
    this.streamInterval = setInterval(() => {
      this.fetchLiveLogs();
    }, 2000); // Ping server every 2 seconds
  }

  fetchLiveLogs() {
    this.http.get<any>(`${environment.apiUrl}/audit/stream`).subscribe({
      next: (res) => {
        // Format the Java timestamps for the UI
        const formattedLogs = (res.data || []).map((log: any) => ({
            ...log,
            timestamp: new Date(log.timestamp).toISOString().replace('T', ' ').substring(0, 19)
        }));
        this.logs.set(formattedLogs);
      },
      error: (err) => console.warn('Terminal feed disconnected.', err)
    });
  }

  triggerManualRefund() {
    const payload = { accountId: 'C004', amount: 450.00 };
    
    this.http.post(`${environment.apiUrl}/audit/force-refund`, payload).subscribe({
      next: () => this.fetchLiveLogs(), // Instantly fetch the new glitch log!
      error: (err) => console.error(err)
    });
  }

  clearLogs() {
    this.http.delete(`${environment.apiUrl}/audit/clear`).subscribe({
      next: () => {
        this.logs.set([]);
        this.previousLogCount = 0;
        this.fetchLiveLogs(); // Fetch the "Buffer Cleared" log
      },
      error: (err) => console.error(err)
    });
  }

  // =========================================================================
  // UI HELPERS (Maps exactly to Java entities now)
  // =========================================================================
  getLogColor(level: string): string {
    switch (level) {
      case 'INFO': return 'text-emerald-400';
      case 'WARN': return 'text-amber-400';
      case 'CRITICAL': return 'text-rose-500 font-bold';
      case 'REFUND': return 'text-fuchsia-400 font-bold';
      default: return 'text-slate-300';
    }
  }

  getBgColor(level: string): string {
    if (level === 'CRITICAL') return 'bg-rose-500/10 border-l-[3px] border-rose-500';
    if (level === 'REFUND') return 'bg-fuchsia-500/10 border-l-[3px] border-fuchsia-500';
    return 'border-l-[3px] border-transparent hover:bg-slate-800/30';
  }
}