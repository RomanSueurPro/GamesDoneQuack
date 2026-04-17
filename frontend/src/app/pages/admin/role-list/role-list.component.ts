import { Component, Input } from '@angular/core';
import { RoleWithoutPermissions } from '../../../models/RoleWithoutPermissions';
import { catchError, map, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RoleAllFields } from '../../../models/RoleAllFields';
import { MatListModule } from '@angular/material/list';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { PermissionAllFields } from '../../../models/PermissionAllFields';
import { PermissionWithoutRoles } from '../../../models/PermissionWithoutRoles';
import { API_ENDPOINTS } from '../../../config/api-endpoints';

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [MatListModule, FormsModule, ReactiveFormsModule,
  ],
  templateUrl: './role-list.component.html',
  styleUrl: './role-list.component.scss'
})
export class RoleListComponent {

  constructor(private http: HttpClient){
    this.form = new FormGroup({
      roles: this.rolesControl,
    });
    this.hideSingleSelectionIndicator = false;
  }

  @Input()
  hideSingleSelectionIndicator: boolean;

  form: FormGroup;
  public arrayRoles: RoleAllFields[] = [];
  public arrayPermissions: PermissionWithoutRoles[] = [];
  rolesControl = new FormControl();

  public associatedPermissions: PermissionWithoutRoles[] = [];
  public notAssociatedPermissions: PermissionWithoutRoles[] = [];

  ngOnInit(){
    this.loadRoles();
    this.loadPermissions();
    
  }

  refreshObject(){
    this.loadRoles();
  }

  fetchRolesObservable(){
    return this.http.get<RoleAllFields[]>(API_ENDPOINTS.admin.fetchRoles, {withCredentials: true});
  }

  

  loadRoles(){
    this.fetchRolesObservable().subscribe({
      next: (result) => 
        {
          this.arrayRoles = result;
        },
      error: (error) => console.log(error)
    })
  }

  fetchPermissionsObservable(){
    return this.http.get<PermissionWithoutRoles[]>(API_ENDPOINTS.admin.fetchPermissions, {withCredentials: true});
  }

  loadPermissions(){
    this.fetchPermissionsObservable().subscribe({
      next: (result) => 
        {
          this.arrayPermissions = result;
        },
      error: (error) => console.log(error)
    })
  }

  getPermissionsAssociations(role: RoleAllFields){
  
    this.associatedPermissions = [];
    this.notAssociatedPermissions = [];

    const ids = [];
    for(let rolePermission of role.permissions){
      ids.push(rolePermission.id);
    }
    for(let permission of this.arrayPermissions){
      if(ids.includes(permission.id)){
        this.associatedPermissions.push(permission);
      }else{       
        this.notAssociatedPermissions.push(permission);
      }
    }
  }

  testWithButton(role: RoleAllFields){
    console.log(this.arrayRoles);
    this.getPermissionsAssociations(role);
    
  }
  
}


