import { Component, ElementRef, Input } from '@angular/core';
import { concat, concatMap, forkJoin, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RoleAllFields } from '../../../models/RoleAllFields';
import { MatListModule, MatSelectionList } from '@angular/material/list';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import { PermissionWithoutRoles } from '../../../models/PermissionWithoutRoles';
import { API_ENDPOINTS } from '../../../config/api-endpoints';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
import { ViewChild } from '@angular/core';


@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [MatListModule, FormsModule, ReactiveFormsModule, MatCheckboxModule],
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

  constructor(private http: HttpClient, private snackbar: MatSnackBar, private dialog: MatDialog){
    this.hideSingleSelectionIndicator = false;
    this.selected = false;
    this.arrayRoles = [];
  }

  //@Input directives are the way to enforce API options from Angular Material
  @Input()
  hideSingleSelectionIndicator: boolean;
  @Input()
  selected: boolean;

  @ViewChild('roleList') roleList!: MatSelectionList;
  @ViewChild('newRoleDiv') newRoleDiv!: ElementRef;
  @ViewChild('buttonNewRole') buttonNewRole!: ElementRef;
  @ViewChild('newRoleButtonDiv') newRoleButtonDiv!: ElementRef;

  

  
  public arrayRoles: RoleAllFields[];
  public arrayPermissions: PermissionWithoutRoles[] = [];
  
  public associatedPermissions: PermissionWithoutRoles[] = [];
  public notAssociatedPermissions: PermissionWithoutRoles[] = [];

  newPermissionField: string = "";
  newRoleField: string = "";

  get selectedRole(): RoleAllFields | null {
    const id = this.form.get('id')?.value;
    return this.arrayRoles.find(r => r.id === id) ?? null;
  }

  ngOnInit(){
    this.loadDataObservable().subscribe({
      error: (error) => console.log(error), 
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
          this.updateFullForm(role);
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
        });
  }

  onSelectionChange(event: any) {
    const selected: RoleAllFields = event.options[0]?.value;
    if(!this.checkUnsavedModificationsOnRole()){
      this.updateFullForm(selected);
    }else{
      this.openUnsavedDialog(selected);
    } 
  }

  updateFullForm(selected: RoleAllFields){
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

  openUnsavedDialog(selected: RoleAllFields) {
    const dialogRef = this.dialog.open(
      ConfirmationDialogComponent,
      {
        width: '25rem',
        height: '5rem',
        hasBackdrop: true,
        disableClose: true,
        panelClass: 'confirmation-dialog',
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        //user confirms he wants to leave
        this.updateFullForm(selected);
      }
      else {
        if(this.form.value.id){
          this.roleList.options.find(option => option.value.id === this.form.value.id)?.toggle();
          this.form.markAsDirty();
        }
      }
    });
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
    });
    this.form.markAsDirty();
    
  }

  cancelChanges():void{
    let role = undefined;
    if(this.form.value.id){
      role = this.arrayRoles.find((r) => r.id === this.form.value.id);
    }
      
    if(role){
      this.updatePermissionsAssociations(role);
      this.updateFullForm(role);
      this.form.markAsPristine();
    }
  }

  saveChangesObservable(){
    if(this.form.value.id !== null && this.form.value.id !== undefined && this.form.value.id >= 0){
      return this.http.patch(API_ENDPOINTS.admin.updateRole, this.form.value, 
      {withCredentials: true});
    }
    return this.http.post(API_ENDPOINTS.admin.createRole, this.form.value, 
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
        this.form.markAsPristine();
      },
      error: (error) => {
        console.log(error);
        this.snackbar.open(
          'Code status : ' + error.error.status + ', ' + error.error.error,
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
    this.form.controls.defaultRole.enable();
    if(this.form.value.adminRole || this.form.value.defaultRole){
      this.form.controls.defaultRole.disable();
    }
  }

  checkUnsavedModificationsOnRole(): boolean{
    if(this.form.dirty){
      this.form.markAsPristine();
      return true;
    }
    return false;
  }

  showNewRoleField(){
    this.newRoleDiv.nativeElement.style.display = 'flex';
    this.newRoleButtonDiv.nativeElement.style.display = 'none';
    
  }

  hideNewRoleField(){
    this.newRoleDiv.nativeElement.style.display = 'none';
    this.newRoleButtonDiv.nativeElement.style.display = 'flex';
  }

  makeNewRole(){
    let role: RoleAllFields = {
      id: -1,
      name: this.newRoleField,
      adminRole: false,
      defaultRole: false,
      permissions: [],
    };
    this.arrayRoles.push(role);
    this.updateFullForm(role);
    this.hideNewRoleField();
  }
  
}


