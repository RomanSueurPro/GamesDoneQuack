import { Component, Input } from '@angular/core';
import { concat, concatMap, forkJoin, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RoleAllFields } from '../../../models/RoleAllFields';
import { MatListModule } from '@angular/material/list';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { PermissionWithoutRoles } from '../../../models/PermissionWithoutRoles';
import { API_ENDPOINTS } from '../../../config/api-endpoints';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCheckboxModule } from '@angular/material/checkbox';

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [MatListModule, FormsModule, ReactiveFormsModule, MatCheckboxModule
  ],
  templateUrl: './role-list.component.html',
  styleUrl: './role-list.component.scss'
})
export class RoleListComponent {

  form = new FormGroup({
    id: new FormControl<number | null>(-1),
    name: new FormControl<string>(''),
    permissions: new FormControl<PermissionWithoutRoles[]>([]),
    adminRole: new FormControl<boolean>(false),
    defaultRole: new FormControl<boolean>(false),
  });

  constructor(private http: HttpClient, private snackbar: MatSnackBar){
    this.hideSingleSelectionIndicator = false;
    this.selected = false;
    this.arrayRoles = [];
  }

  //@Input directives are the way to enforce API options from Angular Material
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
        this.sortAlphabetically(this.arrayRoles);
        this.arrayPermissions = permissions;
        this.sortAlphabetically(this.arrayPermissions);

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
            adminRole: role.adminRole,
            defaultRole: role.defaultRole
          });
        }
        this.defaultCheckBoxToggle();
      },
      error: (error) => {
        console.log('An error occured during component initialization : ');
        console.log(error);
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
            adminRole: role.adminRole,
            defaultRole: role.defaultRole
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

  //elements of the array must have a name property
  sortAlphabetically(array: Array<any>){
    array.sort((a, b) => a.name.localeCompare(b.name));
  }

  insertPermission(permission: PermissionWithoutRoles, permissionList: PermissionWithoutRoles[]){
    let low = 0;
    let high = permissionList.length;

    while (low < high) {
    const mid = Math.floor((low + high) / 2);

    if (permissionList[mid].name.localeCompare(permission.name) < 0) {
      low = mid + 1;
    } else {
      high = mid;
    }
  }

  permissionList.splice(low, 0, permission);
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
        this.insertPermission(permission, this.associatedPermissions);
      }else{       
        this.insertPermission(permission, this.notAssociatedPermissions);
      }
    }
    //permissions in alphabetical order
    this.sortAlphabetically(this.associatedPermissions);
    this.sortAlphabetically(this.notAssociatedPermissions);
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
          adminRole: selected.adminRole,
          defaultRole: selected.defaultRole,
          permissions: selected.permissions,
        });
    this.defaultCheckBoxToggle();
  }

  togglePermission(permissionName: string){
    const permissionObject: PermissionWithoutRoles = this.arrayPermissions.filter((permission) => permission.name === permissionName)[0];
    if(this.associatedPermissions.includes(permissionObject)){
      const index = this.associatedPermissions.indexOf(permissionObject, 0);
      if(index > -1){
        this.associatedPermissions.splice(index, 1);
        this.insertPermission(permissionObject, this.notAssociatedPermissions);
      }
    }
    else{
      const index = this.notAssociatedPermissions.indexOf(permissionObject, 0);
      if(index > -1){
        this.notAssociatedPermissions.splice(index, 1);
        this.insertPermission(permissionObject, this.associatedPermissions);
      }
    }
    this.sortAlphabetically(this.associatedPermissions);
    this.sortAlphabetically(this.notAssociatedPermissions);
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
        adminRole: role.adminRole,
        defaultRole: role.defaultRole
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
      next: () => {
        this.snackbar.open(
        'Role update successfull',
        'Close',
        {
          duration: 3000,
          panelClass: ['success-snackbar'],
        }
        );
      },
      error: (error) => {
        console.log(error);
        this.snackbar.open(
        'Error occured' + '\n' + error,
        'Close',
        {
          panelClass: ['failure-snackbar'],
        }
        );
      },
    })
  }

  associateNewPermission(){
    let perm: PermissionWithoutRoles = {id: -1, name: this.newPermissionField};
    this.insertPermission(perm, this.associatedPermissions);
    this.insertPermission(perm, this.arrayPermissions);
    this.form.patchValue({
      permissions: this.associatedPermissions,
    })
  }

  defaultCheckBoxToggle(){
    if(this.form.value.adminRole || this.form.value.defaultRole){
      this.form.controls.defaultRole.disable();
    }else{
      this.form.controls.defaultRole.enable();
    }
  }
  
}


