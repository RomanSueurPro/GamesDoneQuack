import { Component } from '@angular/core';
import { RoleListComponent } from './role-list/role-list.component';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [RoleListComponent],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.scss'
})
export class AdminComponent {

}
