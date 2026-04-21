import { Component, Input } from '@angular/core';
import { forkJoin } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RoleAllFields } from '../../../models/RoleAllFields';
import { MatListModule } from '@angular/material/list';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
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
      rolename: new FormControl(''),
    });
    
    this.hideSingleSelectionIndicator = false;
    this.selected = false;
    this.arrayRoles = [];
  }
  
  @Input()
  hideSingleSelectionIndicator: boolean;

  @Input()
  selected: boolean;

  selectedRole: any = null;
  form: FormGroup;
  public arrayRoles: RoleAllFields[];
  public arrayPermissions: PermissionWithoutRoles[] = [];
  rolesControl = new FormControl();
  rolenametextfield: string = "";
  

  public associatedPermissions: PermissionWithoutRoles[] = [];
  public notAssociatedPermissions: PermissionWithoutRoles[] = [];

  ngOnInit(){
    this.loadData();
  }

  loadData(){
    forkJoin({
      permissions: this.fetchPermissionsObservable(),
      roles: this.fetchRolesObservable(),
    }).subscribe({
      next: ({permissions, roles}) => {
        this.arrayRoles = roles;
        this.selectedRole = this.arrayRoles[0] ?? null;
        this.arrayPermissions = permissions;
        this.updatePermissionsAssociations(this.selectedRole);
      },
      error: () => {
        console.log('An error occured during component initialization')
      }
    });
  }

  fetchRolesObservable(){
    return this.http.get<RoleAllFields[]>(API_ENDPOINTS.admin.fetchRoles, {withCredentials: true});
  }

  fetchPermissionsObservable(){
    return this.http.get<PermissionWithoutRoles[]>(API_ENDPOINTS.admin.fetchPermissions, {withCredentials: true});
  }

  updatePermissionsAssociations(role: RoleAllFields){
  
    if(role ===null){
      return;
    }
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

  onSelectionChange(event: any) {
    const selected = event.options[0]?.value;
    this.selectedRole = selected;
    this.updatePermissionsAssociations(selected);
  }

  togglePermission(permissionName: string){
    const permissionObject = this.arrayPermissions.filter((permission) => permission.name === permissionName)[0];
    
    if(this.associatedPermissions.includes(permissionObject)){
      const index = this.associatedPermissions.indexOf(permissionObject, 0);
      if(index > -1){
        this.associatedPermissions.splice(index, 1);
        this.notAssociatedPermissions.push(permissionObject);
      }
    }
    else{
      const index = this.notAssociatedPermissions.indexOf(permissionObject, 0);
      if(index > -1){
        this.notAssociatedPermissions.splice(index, 1);
        this.associatedPermissions.push(permissionObject);
      }
    }
  }

  cancelChanges(role: RoleAllFields):void{
    this.updatePermissionsAssociations(role);
    this.rolenametextfield = role.name;
  }

  saveChanges(){

  }

}


