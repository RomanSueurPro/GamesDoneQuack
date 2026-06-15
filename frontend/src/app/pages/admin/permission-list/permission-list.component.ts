import { Component, ElementRef, HostListener, Input } from '@angular/core';
import { concatMap, forkJoin, of, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { MatListModule, MatSelectionList } from '@angular/material/list';
import { AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, ValidationErrors, ValidatorFn } from '@angular/forms';
import { API_ENDPOINTS } from '../../../config/api-endpoints';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { ViewChild } from '@angular/core';
import { DeleteDialogComponent } from '../delete-dialog/delete-dialog.component';
import { RoleWithoutPermissions } from '../../../models/RoleWithoutPermissions';
import { PermissionAllFields } from '../../../models/PermissionAllFields';
import { SnackbarService } from '../../../services/snackbar.service';

@Component({
  selector: 'app-permission-list',
  standalone: true,
  imports: [MatListModule, FormsModule, ReactiveFormsModule, MatCheckboxModule],
  templateUrl: './permission-list.component.html',
  styleUrl: './permission-list.component.scss'
})
export class PermissionListComponent {

form = new FormGroup({
    id: new FormControl<number | null>(-1),
    name: new FormControl<string>('', [this.permissionNameValidator()]),
    roles: new FormControl<RoleWithoutPermissions[]>([]),
    
  });

  constructor(private http: HttpClient, private snackBarService: SnackbarService, private confirmDialog: MatDialog, private deleteDialog: MatDialog){
    this.hideSingleSelectionIndicator = false;
    this.selected = false;
    this.arrayPermissions = [];
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

  @ViewChild('permissionList') permissionList!: MatSelectionList;
  @ViewChild('newPermissionDiv') newPermissionDiv!: ElementRef;
  @ViewChild('buttonNewPermissino') buttonNewPermission!: ElementRef;
  @ViewChild('newPermissionButtonDiv') newPermissionButtonDiv!: ElementRef;

  @HostListener('document:mousedown', ['$event'])
  onGlobalClick(event: MouseEvent) {
    if (this.clickedInside || !this.blurEventActive) {
      this.clickedInside = false;
      return;
    }
    this.hideNewPermissionField();
  }

  private blurEventActive: boolean = true;
  private dialogOptions = {width: '75rem', height: '15rem', hasBackdrop: true, disableClose: true};

  public arrayPermissions: PermissionAllFields[];
  public arrayRoles: RoleWithoutPermissions[] = [];
  
  public associatedRoles: RoleWithoutPermissions[] = [];
  public notAssociatedRoles: RoleWithoutPermissions[] = [];

  newPermissionField: string = "";
  lastCreatedPermissionId: number = -1;
  clickedInside: boolean = false;

  get isNewPermissionNameValid():boolean {
    const name = this.newPermissionField.toUpperCase().replace(/\s/g, "");
    return name !== '';
  }

  get selectedPermission(): PermissionAllFields | null {
    const id = this.form.get('id')?.value;
    return this.arrayPermissions.find(p => p.id === id) ?? null;
  }

  ngOnInit(){
    this.loadDataObservable().subscribe({
      error: (error) => console.log(error), 
    });
  } 

  loadDataObservable(){
    const previousId = this.form.get('id')?.value;
    
    return forkJoin({
      roles: this.fetchRolesObservable(),
      permissions: this.fetchPermissionsObservable(),
    }).pipe(
      tap(({ roles, permissions }) => {
        this.arrayRoles = roles;
        this.sortAlphabetically(this.arrayRoles);
        this.arrayPermissions = permissions;
        this.sortAlphabetically(this.arrayPermissions);

        // Try to restore previous selection
        let permission = this.arrayPermissions.find(p => p.id === previousId);

        //we did create an element before
        if(!permission && this.lastCreatedPermissionId !== -1){
          permission = this.arrayPermissions.find(p => p.id === this.lastCreatedPermissionId);
        }

        // Fallback for first load
        if (!permission) {
          permission = this.arrayPermissions[0] ?? null;
        }

        if (permission) {
          this.updateRolesAssociations(permission);
          this.updateFullForm(permission);
        }
      })
    );
  }

  fetchRolesObservable(){
    return this.http.get<RoleWithoutPermissions[]>(API_ENDPOINTS.admin.fetchAllRolesNoPermissionField, {withCredentials: true});
  }

  fetchPermissionsObservable(){
    return this.http.get<PermissionAllFields[]>(API_ENDPOINTS.admin.fetchAllPermissions, {withCredentials: true});
  }

  //elements of the array must have a name property
  sortAlphabetically(array: Array<any>){
    array.sort((a, b) => a.name.localeCompare(b.name));
  }

  insertRole(role: RoleWithoutPermissions, roleList: RoleWithoutPermissions[]){
    let low = 0;
    let high = roleList.length;

    while (low < high) {
      const mid = Math.floor((low + high) / 2);

      if (roleList[mid].name.localeCompare(role.name) < 0) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }

    roleList.splice(low, 0, role);
  }

  updateRolesAssociations(permission: PermissionAllFields){
  
    this.arrayRoles = this.arrayRoles.filter((r) => r.id !== -1);
    if(permission === null){
      return;
    }
    this.associatedRoles = [];
    this.notAssociatedRoles = [];

    const ids = [];
    for(let permissionRole of permission.roles){
      ids.push(permissionRole.id);
    }
    for(let role of this.arrayRoles){
      if(ids.includes(role.id)){
        this.insertRole(role, this.associatedRoles);
      }else{       
        this.insertRole(role, this.notAssociatedRoles);
      }
    }
    //permissions in alphabetical order
    this.sortAlphabetically(this.associatedRoles);
    this.sortAlphabetically(this.notAssociatedRoles);
    this.form.patchValue({
          roles: this.associatedRoles
        });
  }

  onSelectionChange(event: any) {
    const selected: PermissionAllFields = event.options[0]?.value;
    if(!this.checkUnsavedModificationsOnPermission()){
      this.updateFullForm(selected);
    }else{
      this.openUnsavedDialog(selected);
    } 
  }

  updateFullForm(selected: PermissionAllFields){
    this.updateRolesAssociations(selected);
      this.form.patchValue({
          id: selected.id,
          name: selected.name,
          roles: selected.roles,
      });
  }

  openUnsavedDialog(selected: PermissionAllFields) {
    const dialogRef = this.confirmDialog.open(
      ConfirmationDialogComponent,
      {
        width: this.dialogOptions.width,
        height: this.dialogOptions.height,
        hasBackdrop: this.dialogOptions.hasBackdrop,
        disableClose: this.dialogOptions.disableClose,
        panelClass: 'confirmation-dialog',
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        //user confirms he wants to leave
        this.arrayPermissions = this.arrayPermissions.filter((p) => p.id !== -1);
        this.updateFullForm(selected);
      }
      else {
        if(this.form.value.id){
          this.permissionList.options.find(option => option.value.id === this.form.value.id)?.toggle();
          this.form.markAsDirty();
        }
      }
    });
  }

  toggleRole(roleName: string){
    const roleObject: RoleWithoutPermissions = this.arrayRoles.filter((role) => role.name === roleName)[0];
    if(this.associatedRoles.includes(roleObject)){
      const index = this.associatedRoles.indexOf(roleObject, 0);
      if(index > -1){
        this.associatedRoles.splice(index, 1);
        this.insertRole(roleObject, this.notAssociatedRoles);
      }
    }
    else{
      const index = this.notAssociatedRoles.indexOf(roleObject, 0);
      if(index > -1){
        this.notAssociatedRoles.splice(index, 1);
        this.insertRole(roleObject, this.associatedRoles);
      }
    }
    this.sortAlphabetically(this.associatedRoles);
    this.sortAlphabetically(this.notAssociatedRoles);
    this.form.patchValue({
      roles: this.associatedRoles,
    });
    this.form.markAsDirty();
    
  }

  cancelChanges():void{
    let permission = undefined;
    if(this.form.value.id){
      permission = this.arrayPermissions.find((p) => p.id === this.form.value.id);
    }
      
    if(permission){
      if(permission.id === -1){
        this.arrayPermissions = this.arrayPermissions.filter((p) => p.id !== -1);
        permission = this.arrayPermissions[0];
      }
      this.updateRolesAssociations(permission);
      this.updateFullForm(permission);
      this.form.markAsPristine();
    }
  }

  saveChangesObservable(){
    //validation
    if(this.form.value.id !== null && this.form.value.id !== undefined && this.form.value.id >= 0){
      return this.http.patch(API_ENDPOINTS.admin.updatePermission, this.form.value, 
      {withCredentials: true});
    }
    return this.http.post(API_ENDPOINTS.admin.createPermission, this.form.value, 
      {withCredentials: true});
  }

  completeProcedure(){
    let savedPermission: any;
    of(null).pipe(
      concatMap(() => this.saveChangesObservable()),
      tap((response) => {
        savedPermission = response;
        this.lastCreatedPermissionId = savedPermission.id;
      }),
      concatMap(() => this.loadDataObservable()),
    ).subscribe({
      next: () => {
        this.snackBarService.showSuccessMessageSnackBar('Permission update successfull');
        this.form.markAsPristine();
      },
      error: (error) => {
        console.log(error);
        this.snackBarService.showErrorSnackBar(error);
      },
    })
  }

  checkUnsavedModificationsOnPermission(): boolean{
    if(this.form.dirty){
      this.form.markAsPristine();
      return true;
    }
    return false;
  }

  showNewPermissionField(){
    if(this.selectedPermission !==  null && this.form.dirty){
      this.openUnsavedDialogForCreation(this.selectedPermission);
    }

    //It is fine if those execute while user makes choice in dialog
    this.newPermissionDiv.nativeElement.style.display = 'flex';
    this.newPermissionButtonDiv.nativeElement.style.display = 'none';
    this.newPermissionDiv.nativeElement.children[0].focus();
  }

  hideNewPermissionField(){
    this.newPermissionDiv.nativeElement.style.display = 'none';
    this.newPermissionButtonDiv.nativeElement.style.display = 'flex';
    this.newPermissionField = "";
  }

  makeNewPermission(){
    
    let roleAdmin: RoleWithoutPermissions|undefined = this.arrayRoles.find((r) => r.adminRole);
    let permission: PermissionAllFields = {
      id: -1,
      name: this.newPermissionField,
      roles: [],
    };
    if(roleAdmin !== undefined){
      permission.roles.push(roleAdmin);
    }
    this.arrayPermissions.push(permission);
    this.updateFullForm(permission);
    this.hideNewPermissionField();
    this.newPermissionField = "";
    this.form.markAsDirty();
  }

  deletePermission():void{
    this.openDeleteDialog();
  }
  
  openDeleteDialog(){
    const dialogRef = this.deleteDialog.open(
      DeleteDialogComponent,
      {
        width: this.dialogOptions.width,
        height: this.dialogOptions.height,
        hasBackdrop: this.dialogOptions.hasBackdrop,
        disableClose: this.dialogOptions.disableClose,
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
        this.http.delete(API_ENDPOINTS.admin.deletePermission, {
          withCredentials: true,
          body: this.form.value
        }).subscribe({
          next: (response:any) => {
            this.arrayPermissions = this.arrayPermissions.filter((p) => p.id !== this.form.value.id);
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

  permissionNameValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {

      const value = (control.value ?? '').toString().toUpperCase().replace(/\s/g, "");

      const nameRegex = /^(?!_)[A-Za-z0-9_]{1,244}$/;
      const isValid = nameRegex.test(value);

      const isInvalid =
        value === '' 
        || value === '_PERMISSION'
        || !isValid;

      return isInvalid ? { invalidPermissionName: true } : null;
    };
  }

  openUnsavedDialogForCreation(permission: PermissionAllFields) {
    this.blurEventActive = false;
    const dialogRef = this.confirmDialog.open(
      ConfirmationDialogComponent,
      {
        width: this.dialogOptions.width,
        height: this.dialogOptions.height,
        hasBackdrop: this.dialogOptions.hasBackdrop,
        disableClose: this.dialogOptions.disableClose,
        panelClass: 'confirmation-dialog',
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        if(permission.id === -1){
          permission = this.arrayPermissions[0] ?? null;
          this.updateFullForm(permission);
        }
        this.arrayPermissions = this.arrayPermissions.filter((p) => p.id !== -1);
        this.newPermissionDiv.nativeElement.children[0].focus();
        this.updateRolesAssociations(permission);
        this.form.markAsPristine();
        this.blurEventActive = true;
      }else{
        if(this.form.value.id){
          this.hideNewPermissionField();
        }
      }
    });
  }
  
}
