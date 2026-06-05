import { Component, ViewChild } from '@angular/core';
import { RoleListComponent } from './role-list/role-list.component';
import { MatTabChangeEvent, MatTabsModule } from '@angular/material/tabs';
import { HeaderComponent } from '../../header/header.component'; 
import { PermissionListComponent } from './permission-list/permission-list.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [RoleListComponent, MatTabsModule, HeaderComponent, PermissionListComponent],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent {
  //reload logic so tabs are up to date with database
  selectedTab = 0;

  onTabChanged(event: MatTabChangeEvent): void {
    this.selectedTab = event.index;
  }

}
