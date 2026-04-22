import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterModule], // This enables the router links
  templateUrl: './sidebar.html'
})
export class Sidebar {}