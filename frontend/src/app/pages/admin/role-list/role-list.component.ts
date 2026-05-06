import { Component, Input } from '@angular/core';
import { concat, concatMap, forkJoin, of, tap } from 'rxjs';
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

  form = new FormGroup({
    id: new FormControl<number | null>(-1),
    name: new FormControl<string>(''),
    permissions: new FormControl<PermissionWithoutRoles[]>([]),
    isAdmin: new FormControl<boolean>(false),
    isDefault: new FormControl<boolean>(false),
  });

  constructor(private http: HttpClient){
    this.hideSingleSelectionIndicator = false;
    this.selected = false;
    this.arrayRoles = [];
  }

  @Input()
  hideSingleSelectionIndicator: boolean;

  @Input()
  selected: boolean;
  
  public arrayRoles: RoleAllFields[];
  public arrayPermissions: PermissionWithoutRoles[] = [];
  
  public associatedPermissions: PermissionWithoutRoles[] = [];
  public notAssociatedPermissions: PermissionWithoutRoles[] = [];

  newPermissionField: string = "";
  

  get selectedRole(): RoleAllFields | null {
    const id = this.form.get('id')?.value;
    return this.arrayRoles.find(r => r.id === id) ?? null;
  }

  ngOnInit(){
    this.loadData();
  }

  loadData() {
    const previousId = this.form.get('id')?.value;

    forkJoin({
      permissions: this.fetchPermissionsObservable(),
      roles: this.fetchRolesObservable(),
    }).subscribe({
      next: ({ permissions, roles }) => {
        this.arrayRoles = roles;
        this.arrayPermissions = permissions;

        // Try to restore previous selection
        let role = this.arrayRoles.find(r => r.id === previousId);

        // Fallback for first load
        if (!role) {
          role = this.arrayRoles[0] ?? null;
        }

        this.updatePermissionsAssociations(role);

        if (role) {
          this.form.patchValue({
            id: role.id,
            name: role.name,
            permissions: role.permissions,
            isAdmin: role.isAdmin,
            isDefault: role.isDefault
          });
        }
      },
      error: (error) => {
        console.log('An error occured during component initialization : ' + error)
      }
    });
  }

  loadDataObservable(){
    const previousId = this.form.get('id')?.value;

    return forkJoin({
      permissions: this.fetchPermissionsObservable(),
      roles: this.fetchRolesObservable(),
    }).pipe(
      tap(({ permissions, roles }) => {
        this.arrayRoles = roles;
        this.arrayPermissions = permissions;

        // Try to restore previous selection
        let role = this.arrayRoles.find(r => r.id === previousId);

        // Fallback for first load
        if (!role) {
          role = this.arrayRoles[0] ?? null;
        }

        this.updatePermissionsAssociations(role);

        if (role) {
          this.form.patchValue({
            id: role.id,
            name: role.name,
            permissions: role.permissions,
            isAdmin: role.isAdmin,
            isDefault: role.isDefault
          });
        }
      })
    );
  }

  fetchRolesObservable(){
    return this.http.get<RoleAllFields[]>(API_ENDPOINTS.admin.fetchAllRoles, {withCredentials: true});
  }

  fetchPermissionsObservable(){
    return this.http.get<PermissionWithoutRoles[]>(API_ENDPOINTS.admin.fetchAllPermissionsNoRoleField, {withCredentials: true});
  }

  updatePermissionsAssociations(role: RoleAllFields){
  
    this.arrayPermissions = this.arrayPermissions.filter((p) => p.id !== -1);
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
    this.form.patchValue({
          permissions: this.associatedPermissions
        })
  }

  onSelectionChange(event: any) {
    const selected: RoleAllFields = event.options[0]?.value;

    this.updatePermissionsAssociations(selected);
    this.form.patchValue({
          id: selected.id,
          name: selected.name,
          isAdmin: selected.isAdmin,
          isDefault: selected.isDefault,
          permissions: selected.permissions,
        });
  }

  togglePermission(permissionName: string){
    const permissionObject: PermissionWithoutRoles = this.arrayPermissions.filter((permission) => permission.name === permissionName)[0];
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
    this.form.patchValue({
      permissions: this.associatedPermissions,
    })
  }

  cancelChanges(roleId: number|null|undefined):void{
    let role = undefined;
    if(roleId){
      role = this.arrayRoles.find((r) => r.id = roleId);
    }
    if(role){
      this.updatePermissionsAssociations(role);
      this.form.patchValue({
        name: role.name,
        permissions: role.permissions,
        isAdmin: role.isAdmin,
        isDefault: role.isDefault
      });
    }
  }

  saveChanges(){
    console.log(this.form.value);
    this.http.patch(API_ENDPOINTS.admin.updateRole, this.form.value, 
    {withCredentials: true,

    }).subscribe({
      next: () => {
        console.log("patchin went through");
        this.loadData();
      },
      error: (error) => console.log("error" + error),
      });
  }

  saveChangesObservable(){
    console.log(this.form.value);
    return this.http.patch(API_ENDPOINTS.admin.updateRole, this.form.value, 
    {withCredentials: true});
  }

  completeProcedure(){
    
    of(null).pipe(
      concatMap(() => this.saveChangesObservable()),
      concatMap(() => this.loadDataObservable()),
    ).subscribe({
      error: (error) => console.log(error),
    })
  }

  associateNewPermission(){
    let perm: PermissionWithoutRoles = {id: -1, name: this.newPermissionField};
    this.associatedPermissions.push(perm);
    this.arrayPermissions.push(perm);
    this.form.patchValue({
      permissions: this.associatedPermissions,
    })
  }
}


