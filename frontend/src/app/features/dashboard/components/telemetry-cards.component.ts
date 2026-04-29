import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-telemetry-cards',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        
        <div class="opacity-0 animate-fade-in-up bg-slate-900/50 backdrop-blur-md border border-slate-700/50 p-6 rounded-2xl hover:-translate-y-2 hover:border-indigo-500/50 hover:shadow-[0_10px_30px_rgba(99,102,241,0.2)] transition-all duration-300 group" style="animation-delay: 0.2s;">
            <div class="flex justify-between items-start mb-4">
                <div class="p-3 bg-blue-500/10 rounded-xl border border-blue-500/20 group-hover:bg-blue-500/20 transition-colors shadow-[inset_0_0_15px_rgba(59,130,246,0.1)]">
                    <i class="pi pi-car text-2xl text-blue-400 animate-cyber-float inline-block"></i>
                </div>
                <span class="text-xs font-bold text-slate-500 uppercase tracking-widest mt-2">Total Fleet</span>
            </div>
            
            <div class="flex items-baseline">
                <h3 class="text-3xl font-black text-slate-100">{{ telemetry?.totalFleet || 0 }}</h3>
                <span class="text-xs font-bold text-slate-500 ml-2 uppercase tracking-widest flex items-center">
                    Units 
                    <span class="relative flex h-2 w-2 ml-2">
                        <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
                        <span class="relative inline-flex rounded-full h-2 w-2 bg-blue-500"></span>
                    </span>
                </span>
            </div>
            
            <p class="text-emerald-400 text-sm mt-2 flex items-center"><i class="pi pi-arrow-up mr-1 text-xs"></i> 8% from last week</p>
        </div>

        <div class="opacity-0 animate-fade-in-up bg-slate-900/50 backdrop-blur-md border border-slate-700/50 p-6 rounded-2xl hover:-translate-y-2 hover:border-amber-500/50 hover:shadow-[0_10px_30px_rgba(245,158,11,0.2)] transition-all duration-300 group" style="animation-delay: 0.3s;">
            <div class="flex justify-between items-start mb-4">
                <div class="p-3 bg-amber-500/10 rounded-xl border border-amber-500/20 group-hover:bg-amber-500/20 transition-colors shadow-[inset_0_0_15px_rgba(245,158,11,0.1)]">
                    <i class="pi pi-stopwatch text-2xl text-amber-400 animate-cyber-tick inline-block"></i>
                </div>
                <span class="text-xs font-bold text-slate-500 uppercase tracking-widest mt-2">Active Holds</span>
            </div>

            <div class="flex items-baseline">
                <h3 class="text-3xl font-black text-slate-100">{{ telemetry?.activeHolds || 0 }}</h3>
                <span class="text-xs font-bold text-slate-500 ml-2 uppercase tracking-widest flex items-center">
                    Deployed 
                    <span class="relative flex h-2 w-2 ml-2">
                        <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-amber-400 opacity-75" style="animation-duration: 1.5s;"></span>
                        <span class="relative inline-flex rounded-full h-2 w-2 bg-amber-500"></span>
                    </span>
                </span>
            </div>

            <div class="w-full bg-slate-800 rounded-full h-1.5 mt-4 overflow-hidden relative">
                <div class="bg-gradient-to-r from-amber-500 to-orange-400 h-1.5 rounded-full animate-glow-pulse" [style.width.%]="telemetry?.utilizationRate || 0"></div>
            </div>
        </div>

        <div class="opacity-0 animate-fade-in-up bg-slate-900/50 backdrop-blur-md border p-6 rounded-2xl transition-all duration-300 group relative overflow-hidden" 
             [ngClass]="{
                'border-slate-700/50 hover:-translate-y-2 hover:border-purple-500/50 hover:shadow-[0_10px_30px_rgba(168,85,247,0.2)]': surgeMultiplier === 1.0,
                'glow-surge-elevated hover:-translate-y-2': surgeMultiplier === 1.15,
                'animate-surge-peak hover:-translate-y-2': surgeMultiplier === 1.25
             }"
             style="animation-delay: 0.4s;">
            <div class="flex justify-between items-start mb-4 relative z-10">
                <div class="p-3 rounded-xl transition-colors border"
                     [ngClass]="{
                        'bg-purple-500/10 border-purple-500/20 group-hover:bg-purple-500/20 text-purple-400': surgeMultiplier === 1.0,
                        'bg-cyan-500/10 border-cyan-500/30 group-hover:bg-cyan-500/20 text-cyan-400': surgeMultiplier === 1.15,
                        'bg-fuchsia-500/10 border-fuchsia-500/40 group-hover:bg-fuchsia-500/20 text-fuchsia-400': surgeMultiplier === 1.25
                     }">
                    <i class="pi pi-bolt text-2xl animate-cyber-zap inline-block"></i>
                </div>
                <span class="text-xs font-bold text-slate-500 uppercase tracking-widest mt-2">Pricing Engine</span>
            </div>
            
            <h3 class="text-3xl font-black transition-all duration-300 relative z-10"
                [ngClass]="{
                    'text-slate-100': surgeMultiplier === 1.0,
                    'text-cyan-300 drop-shadow-[0_0_10px_rgba(34,211,238,0.8)]': surgeMultiplier === 1.15,
                    'text-fuchsia-300 drop-shadow-[0_0_15px_rgba(217,70,239,0.9)]': surgeMultiplier === 1.25
                }">
                {{ surgeMultiplier === 1.0 ? 'Standard' : (surgeMultiplier === 1.15 ? '+15%' : '+25%') }}
            </h3>
            
            <p class="text-sm mt-2 font-medium transition-colors relative z-10"
               [ngClass]="{
                   'text-purple-400': surgeMultiplier === 1.0,
                   'text-cyan-400/80': surgeMultiplier === 1.15,
                   'text-fuchsia-400/80': surgeMultiplier === 1.25
               }">
                {{ surgeLabel }}
            </p>
        </div>

        <div class="opacity-0 animate-fade-in-up bg-slate-900/50 backdrop-blur-md border border-slate-700/50 p-6 rounded-2xl hover:-translate-y-2 hover:border-emerald-500/50 hover:shadow-[0_10px_30px_rgba(16,185,129,0.2)] transition-all duration-300 group" style="animation-delay: 0.5s;">
            <div class="flex justify-between items-start mb-4">
                <div class="p-3 bg-emerald-500/10 rounded-xl border border-emerald-500/20 group-hover:bg-emerald-500/20 transition-colors shadow-[inset_0_0_15px_rgba(16,185,129,0.1)]">
                    <i class="pi pi-wallet text-2xl text-emerald-400 animate-cyber-flip inline-block"></i>
                </div>
                <span class="text-xs font-bold text-slate-500 uppercase tracking-widest mt-2">Daily Revenue</span>
            </div>
            
            <div class="flex items-baseline">
                <h3 class="text-3xl font-black text-slate-100">{{ telemetry?.dailyRevenue | currency:'USD':'symbol':'1.0-0' }}</h3>
                <span class="text-xs font-bold text-slate-500 ml-2 uppercase tracking-widest">Gross</span>
            </div>
            
            <p class="text-slate-400 text-sm mt-2">Projected to hit $32k by midnight</p>
        </div>
    </div>
  `
})
export class TelemetryCardsComponent {
  @Input({ required: true }) telemetry!: any;
  @Input({ required: true }) surgeMultiplier: number = 1.0;
  @Input({ required: true }) surgeLabel: string = 'Standard Market Rates';
}