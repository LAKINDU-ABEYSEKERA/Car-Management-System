import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-vehicle-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div (click)="clicked.emit(car)" 
         class="cyber-card rounded-xl p-6 relative overflow-hidden group cursor-pointer"
         [ngClass]="{
             'bg-slate-950/40 border border-emerald-900/40 hover:border-emerald-500/50 hover:shadow-[0_0_30px_rgba(16,185,129,0.2)]': car.status === 'AVAILABLE',
             'bg-slate-950/80 border border-rose-900/30 opacity-80 hover:border-rose-500/50 hover:shadow-[0_0_30px_rgba(225,29,72,0.2)]': car.status === 'BOOKED' && urgency === 'SAFE',
             'bg-amber-950/20 border border-amber-500/50 hover:border-amber-400 shadow-[0_0_20px_rgba(245,158,11,0.2)] hover:shadow-[0_0_30px_rgba(245,158,11,0.4)]': car.status === 'BOOKED' && urgency === 'DUE_TODAY',
             'bg-red-950/30 border border-red-500 hover:border-red-400 shadow-[0_0_25px_rgba(220,38,38,0.4)] hover:shadow-[0_0_40px_rgba(220,38,38,0.6)] animate-pulse': car.status === 'BOOKED' && urgency === 'OVERDUE',
             'bg-slate-950/80 border border-slate-800 opacity-50 cursor-not-allowed': car.status === 'MAINTENANCE'
         }">
        
        <div class="hidden group-hover:block absolute left-0 right-0 h-0.5 bg-white shadow-[0_0_15px_rgba(255,255,255,1)] z-20 animate-cyber-scan"
             [ngClass]="{
                'shadow-emerald-400': car.status === 'AVAILABLE',
                'shadow-rose-400': car.status === 'BOOKED' && urgency === 'SAFE',
                'shadow-amber-400': car.status === 'BOOKED' && urgency === 'DUE_TODAY',
                'shadow-red-500': car.status === 'BOOKED' && urgency === 'OVERDUE'
             }"></div>

        @if (car.status === 'AVAILABLE') {
            <div class="absolute inset-0 bg-gradient-to-br from-emerald-500/0 to-emerald-500/5 opacity-0 group-hover:opacity-100 transition-opacity"></div>
        }

        <div class="flex justify-between items-start mb-4 relative z-10">
            <span class="text-sm font-bold text-slate-400 tracking-wider">{{ car.carId }}</span>
            
            @if (car.status === 'AVAILABLE') {
                <div class="px-2 py-1 bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-[10px] uppercase font-bold rounded tracking-widest">Ready</div>
            } @else if (car.status === 'BOOKED') {
                @if (urgency === 'SAFE') {
                    <div class="px-2 py-1 bg-rose-500/10 border border-rose-500/20 text-rose-500 text-[10px] uppercase font-bold rounded tracking-widest flex items-center">
                        <i class="pi pi-lock text-[8px] mr-1"></i> Deployed
                    </div>
                } @else if (urgency === 'DUE_TODAY') {
                    <div class="px-2 py-1 bg-amber-500/20 border border-amber-500/50 text-amber-400 text-[10px] uppercase font-bold rounded tracking-widest flex items-center shadow-[0_0_10px_rgba(245,158,11,0.5)]">
                        <i class="pi pi-clock text-[8px] mr-1"></i> Due Today
                    </div>
                } @else {
                    <div class="px-2 py-1 bg-red-600 border border-red-500 text-white text-[10px] uppercase font-bold rounded tracking-widest flex items-center animate-bounce shadow-[0_0_15px_rgba(220,38,38,0.8)]">
                        <i class="pi pi-exclamation-triangle text-[8px] mr-1"></i> Overdue
                    </div>
                }
            }
        </div>
        
        <h4 class="text-2xl font-black mb-1 relative z-10 transition-colors duration-300" 
            [ngClass]="{
                'text-white group-hover:text-emerald-300': car.status === 'AVAILABLE',
                'text-slate-500 group-hover:text-rose-300': car.status === 'BOOKED' && urgency === 'SAFE',
                'text-amber-100 group-hover:text-amber-300': car.status === 'BOOKED' && urgency === 'DUE_TODAY',
                'text-red-100 group-hover:text-red-300': car.status === 'BOOKED' && urgency === 'OVERDUE'
            }">
            {{ car.brand }} {{ car.model }}
        </h4>
        
        <p class="text-sm mb-6 relative z-10 font-medium" [ngClass]="car.status === 'AVAILABLE' ? 'text-slate-500' : 'text-slate-500'">
            {{ car.fuelType }} | {{ car.seatingCapacity }} Seats
        </p>
        
        <div class="flex justify-between items-end relative z-10">
            <span class="text-xl font-mono font-bold" [ngClass]="car.status === 'AVAILABLE' ? 'text-emerald-400' : 'text-slate-600'">
                @if (car.status === 'AVAILABLE' && surgeMultiplier > 1.0) {
                    <span class="line-through text-slate-500/70 text-sm mr-2">\${{ car.pricePerDay }}</span>
                    <span class="transition-all duration-300"
                          [ngClass]="surgeMultiplier === 1.15 ? 'text-cyan-400 drop-shadow-[0_0_8px_rgba(34,211,238,0.8)]' : 'text-fuchsia-400 animate-text-glow'">
                        \${{ car.pricePerDay * surgeMultiplier | number:'1.0-0' }}
                    </span>
                } @else {
                    \${{ car.pricePerDay }}
                }
                <span class="text-xs font-sans" [ngClass]="car.status === 'AVAILABLE' ? 'text-slate-500' : 'text-slate-600'">/day</span>
            </span>
            
            @if (car.status === 'AVAILABLE') {
                <button class="w-10 h-10 rounded-full bg-emerald-500/10 flex items-center justify-center text-emerald-400 group-hover:bg-emerald-500 group-hover:text-white transition-all duration-300 transform group-hover:scale-110 shadow-[0_0_15px_rgba(16,185,129,0)] group-hover:shadow-[0_0_15px_rgba(16,185,129,0.5)]">
                    <i class="pi pi-arrow-right font-bold"></i>
                </button>
            } @else {
                <i class="pi pi-ban text-2xl group-hover:scale-110 transition-transform" 
                   [ngClass]="{
                      'text-rose-500/30': urgency === 'SAFE',
                      'text-amber-500/50': urgency === 'DUE_TODAY',
                      'text-red-500/70': urgency === 'OVERDUE'
                   }"></i>
            }
        </div>
    </div>
  `
})
export class VehicleCardComponent {
  @Input({ required: true }) car!: any;
  @Input() surgeMultiplier: number = 1.0;
  @Input() urgency: 'SAFE' | 'DUE_TODAY' | 'OVERDUE' = 'SAFE';
  @Output() clicked = new EventEmitter<any>();
}