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

  rolenameControl = new FormControl('');
  idControl = new FormControl(-1);
  permissionsControl = new FormControl();
  isAdminControl = new FormControl(false);
  isDefaultControl = new FormControl(false);

  constructor(private http: HttpClient){
    this.form = new FormGroup({
      name: this.rolenameControl,
      id: this.idControl,
      permissions: this.permissionsControl,
      adminRole: this.isAdminControl,
      defaultRole: this.isDefaultControl,
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
        this.idControl.setValue(this.selectedRole.id);
        this.rolenameControl.setValue(this.selectedRole.name);
      },
      error: () => {
        console.log('An error occured during component initialization')
      }
    });
  }

  fetchRolesObservable(){
    return this.http.get<RoleAllFields[]>(API_ENDPOINTS.admin.fetchAllRoles, {withCredentials: true});
  }

  fetchPermissionsObservable(){
    return this.http.get<PermissionWithoutRoles[]>(API_ENDPOINTS.admin.fetchAllPermissionsNoRoleField, {withCredentials: true});
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
    this.permissionsControl.setValue(this.associatedPermissions);
  }

  onSelectionChange(event: any) {
    const selected = event.options[0]?.value;
    this.selectedRole = selected;
    this.updatePermissionsAssociations(selected);
    this.idControl.setValue(selected.id);
    this.rolenameControl.setValue(this.selectedRole.name);
    this.isAdminControl.setValue(this.selectedRole.isAdmin);
    this.isDefaultControl.setValue(this.selectedRole.isDefault);
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
    this.rolenameControl.setValue(role.name);
  }

  saveChanges(){

  }


  testFormValues(){
    this.http.patch(API_ENDPOINTS.admin.updateRole, this.form.value, 
      {withCredentials: true,

      }).subscribe({
        next: () => console.log("patchin went through"),
        error: (error) => console.log("error" + error),
        });
    console.log(this.form.value);
    console.log(this.form);
    console.log(typeof(this.form.value.id));
  }
}


