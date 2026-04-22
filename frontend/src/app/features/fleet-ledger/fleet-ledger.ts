import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table'; // <-- The PrimeNG Table Engine
import { Fleet } from '../../core/services/fleet';

@Component({
  selector: 'app-fleet-ledger',
  standalone: true,
  imports: [CommonModule, TableModule],
  templateUrl: './fleet-ledger.html' 
})
export class FleetLedger implements OnInit {
  
  fleetService = inject(Fleet);

  ngOnInit() {
      this.fleetService.loadFleet(0, 50); // Override to 50 items for the ledger!
  }
}