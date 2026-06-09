import { Component, ElementRef, HostListener, Input } from '@angular/core';
import { concatMap, forkJoin, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RoleAllFields } from '../../../models/RoleAllFields';
import { MatListModule, MatSelectionList } from '@angular/material/list';
import { AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn } from '@angular/forms';
import { PermissionWithoutRoles } from '../../../models/PermissionWithoutRoles';
import { API_ENDPOINTS } from '../../../config/api-endpoints';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { ViewChild } from '@angular/core';
import { DeleteDialogComponent } from '../delete-dialog/delete-dialog.component';
import { SnackbarService } from '../../../services/snackbar.service';


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
    name: new FormControl<string>('', [this.roleNameValidator()]),
    permissions: new FormControl<PermissionWithoutRoles[]>([]),
    adminRole: new FormControl<boolean>(false),
    defaultRole: new FormControl<boolean>(false),
  });

  constructor(private http: HttpClient, private confirmDialog: MatDialog, private deleteDialog: MatDialog, private snackBarService: SnackbarService){
    this.hideSingleSelectionIndicator = false;
    this.selected = false;
    this.arrayRoles = [];
  }

  //@Input directives are the way to enforce API options from Angular Material
  @Input()
  hideSingleSelectionIndicator: boolean;
  @Input()
  selected: boolean;
  @Input()
  set active(value: boolean) {
    if (value) {
      this.loadDataObservable().subscribe({
        error: (error) => console.log(error.error.error)
      });
    }
  }

  @ViewChild('roleList') roleList!: MatSelectionList;
  @ViewChild('newRoleDiv') newRoleDiv!: ElementRef;
  @ViewChild('buttonNewRole') buttonNewRole!: ElementRef;
  @ViewChild('newRoleButtonDiv') newRoleButtonDiv!: ElementRef;

  @HostListener('document:mousedown', ['$event'])
    onGlobalClick(event: MouseEvent) {
      if (this.clickedInside || !this.blurEventActive) {
        this.clickedInside = false;
        return;
      }
      this.hideNewRoleField();
    }

  public arrayRoles: RoleAllFields[];
  public arrayPermissions: PermissionWithoutRoles[] = [];
  
  public associatedPermissions: PermissionWithoutRoles[] = [];
  public notAssociatedPermissions: PermissionWithoutRoles[] = [];

  private blurEventActive: boolean = true;

  newPermissionField: string = "";
  lastCreatedRoleId: number = -1;
  clickedInside: boolean = false;

  get isNewPermissionNameValid():boolean {
    const name = this.newPermissionField.toUpperCase().replace(/\s/g, "");
    return name !== "_PERMISSION" && name !== '';
  }

  newRoleField: string = "";

  get isNewRoleNameValid():boolean {
    const name = this.newRoleField.toUpperCase().replace(/\s/g, "");
    return name !== "ROLE_" && name !== '';
  }

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

        //we did create an element before
        if(!role && this.lastCreatedRoleId !== -1){
          role = this.arrayRoles.find(r => r.id === this.lastCreatedRoleId);
        }

        // Fallback for first load
        if (!role) {
          role = this.arrayRoles[0] ?? null;
        }

        if (role) {
          this.updatePermissionsAssociations(role);
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
    if(role === null){
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
    const dialogRef = this.confirmDialog.open(
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
        this.arrayRoles = this.arrayRoles.filter((r) => r.id !== -1);
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
      if(role.id === -1){
        this.arrayRoles = this.arrayRoles.filter((r) => r.id !== -1);
        role = this.arrayRoles[0];
      }
      this.updatePermissionsAssociations(role);
      this.updateFullForm(role);
      this.form.markAsPristine();
    }
  }

  saveChangesObservable(){
    //validation
    if(this.form.value.id !== null && this.form.value.id !== undefined && this.form.value.id >= 0){
      return this.http.patch(API_ENDPOINTS.admin.updateRole, this.form.getRawValue(), 
      {withCredentials: true});
    }
    return this.http.post(API_ENDPOINTS.admin.createRole, this.form.value, 
      {withCredentials: true});
  }

  completeProcedure(){
    let savedRole: any;
    of(null).pipe(
      concatMap(() => this.saveChangesObservable()),
      tap((response) => {
        savedRole = response;
        this.lastCreatedRoleId = savedRole.id;
      }),
      concatMap(() => this.loadDataObservable()),
    ).subscribe({
      next: () => {
        this.snackBarService.showSuccessMessageSnackBar('Role update successfull');
        this.form.markAsPristine();
      },
      error: (error) => {
        console.log(error);
        this.snackBarService.showErrorSnackBar(error);
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
    this.newPermissionField ="";
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
    if(this.selectedRole !==  null && this.form.dirty){
      this.openUnsavedDialogForCreation(this.selectedRole);
    }
    this.newRoleDiv.nativeElement.style.display = 'flex';
    this.newRoleButtonDiv.nativeElement.style.display = 'none';
    this.newRoleDiv.nativeElement.children[0].focus();
  }

  hideNewRoleField(){
    this.newRoleDiv.nativeElement.style.display = 'none';
    this.newRoleButtonDiv.nativeElement.style.display = 'flex';
    this.newRoleField = "";
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
    this.form.markAsDirty();
  }

  deleteRole():void{
    this.openDeleteDialog();
  }
  
  openDeleteDialog(){
    const dialogRef = this.deleteDialog.open(
      DeleteDialogComponent,
      {
        width: '25rem',
        height: '5rem',
        hasBackdrop: true,
        disableClose: true,
        panelClass: 'confirmation-dialog',
        data: 
          {
            name: this.form.value.name,
          }
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        //user confirms he wants to remove role from db
        this.http.delete(API_ENDPOINTS.admin.deleteRole, {
          withCredentials: true,
          body: this.form.value
        }).subscribe({
          next: (response:any) => {
            this.arrayRoles = this.arrayRoles.filter((r) => r.id !== this.form.value.id);
            this.loadDataObservable().subscribe({
              error: (error) => console.log(error),         
            });
            this.snackBarService.showSuccessResponseSnackBar(response);
            this.form.markAsPristine();
          },
          error: (error) => {
            console.log(error);
            this.snackBarService.showErrorSnackBar(error);
          }
        })
      }
    });
  }

  roleNameValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {

      const value = (control.value ?? '').toString().toUpperCase().replace(/\s/g, "");

      const isInvalid =
        value === '' ||
        value === 'ROLE_';

      return isInvalid ? { invalidRoleName: true } : null;
    };
  }

  openUnsavedDialogForCreation(role: RoleAllFields) {
    this.blurEventActive = false;
    const dialogRef = this.confirmDialog.open(
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
        if(role.id === -1){
          role = this.arrayRoles[0] ?? null;
          this.updateFullForm(role);
        }
        this.arrayRoles = this.arrayRoles.filter((r) => r.id !== -1);
        this.newRoleDiv.nativeElement.children[0].focus();
        this.updatePermissionsAssociations(role);
        this.form.markAsPristine();
        this.blurEventActive = true;
      }else{
        if(this.form.value.id){
          this.hideNewRoleField();
        }
      }
    });
  }

}