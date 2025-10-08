import { Component } from "@angular/core";
import { MatMenu, MatMenuModule } from "@angular/material/menu";



@Component({
  standalone: true,
  imports: [MatMenuModule],
  selector: 'app-menu-test',
  template: `
    <button [matMenuTriggerFor]="menu">Open menu</button>
    <mat-menu #menu="matMenu" panelClass="'test-menu'">
      <button mat-menu-item>Item 1</button>
    </mat-menu>
  `
})
export class MenuTestComponent {}