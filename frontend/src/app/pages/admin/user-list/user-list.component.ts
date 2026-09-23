import { Component, ElementRef, Input, ViewChild } from '@angular/core';
import { API_ENDPOINTS } from '../../../config/api-endpoints';
import { HttpClient } from '@angular/common/http';
import { MatRow, MatTableModule } from '@angular/material/table';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { UserNoRelations } from '../../../models/UserNoRelations';
import { concatMap, forkJoin, map, Observable, of, pipe, tap } from 'rxjs';
import { UsersResponse } from '../../../models/UsersResponse';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatFormField, MatLabel, MatFormFieldModule, MatFormFieldControl } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelect, MatOption } from '@angular/material/select';
import { RoleWithoutPermissions } from '../../../models/RoleWithoutPermissions';
import { MatDialog } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { MatTab } from '@angular/material/tabs';
import { DatePipe } from '@angular/common';
import { SnackbarService } from '../../../services/snackbar.service';


@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    MatTableModule,
    FormsModule,
    ReactiveFormsModule,
    MatPaginator,
    MatPaginatorModule,
    MatFormField,
    MatLabel,
    MatFormFieldModule,
    MatInputModule,
    MatSelect,
    MatOption,

  ],
  providers: [DatePipe],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {

  defaultPageNumber: number = 0;
  defaultPageSize: number = 3;
  currentPageIndex: number = 0;
  currentPageSize: number = 3;
  totalPages: number = 0;
  pages: number[] = [];

  displayedColumns: string[] = ['username', 'role', 'deleteDate'];
  totalUsers: number = 0;

  searchInput: String = "";

  selectedUser: UserNoRelations | null = null;
  selectedRole: RoleWithoutPermissions | null = null;
  arrayRoles: RoleWithoutPermissions[] = [];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild('inputDate') inputDate!: ElementRef;
  @ViewChild('pageSelect') pageSelect!: ElementRef;

  deleteDateFormatter(deleteDate: Date | null): String {
    let dateString: String = '';
    if (deleteDate === null) {
      dateString = "No date for deletion";
    } else {
      const date = new Date(deleteDate);

      dateString = date.getFullYear() + "-" +
        (date.getMonth() + 1) + "-" +
        date.getDate();
    }

    return dateString;
  }

  selectUser(user: UserNoRelations) {
    this.selectedUser = user;
  }

  form = new FormGroup({
    id: new FormControl<number | null>(-1),
    username: new FormControl<string>(''),
    role: new FormControl<RoleWithoutPermissions>(
      {
        id: -1,
        name: '',
        adminRole: false,
        defaultRole: false,
      }),
    deleteDate: new FormControl<Date | null>(null),
    email: new FormControl<string>(''),
  });

  constructor(
    private http: HttpClient, private confirmDialog: MatDialog, private datepipe: DatePipe,
    private snackBarService: SnackbarService
  ) { }

  @Input()
  set active(value: boolean) {
    if (value) {
      this.loadDataObservable().subscribe();
    }
  }

  public arrayUsers: UserNoRelations[] = [];
  private dialogOptions = { width: '75rem', height: '15rem', hasBackdrop: true, disableClose: true };

  loadDataObservable() {
    return forkJoin({
      usersResponse: this.fetchUsersObservable(this.defaultPageNumber, this.defaultPageSize),
      roles: this.fetchRolesObservable(),
    }).pipe(
      tap(({ usersResponse, roles }) => {
        this.arrayUsers = usersResponse.users;
        this.totalUsers = usersResponse.totalElements;
        this.totalPages = usersResponse.totalPages;
        this.updatePages(usersResponse.totalPages);
        this.arrayRoles = roles;
        if (usersResponse.users.length > 0 && this.selectedUser === null) {
          const firstUser = usersResponse.users[0];
          this.selectedUser = firstUser;
          this.updateFullForm(firstUser);
        }
      })
    );
  }

  fetchRolesObservable() {
    return this.http.get<RoleWithoutPermissions[]>(API_ENDPOINTS.admin.fetchAllRolesNoPermissionField, { withCredentials: true });
  }

  //useful for Search by Username for future selves <3
  testSearch() {
    console.log(this.searchInput);
  }

  onPageChange(event: PageEvent) {
    if (!this.checkUnsavedModificationsOnUser()) {
      this.goToPage(event.pageIndex);
    } else {
      this.openPageChangeDialog(event.pageIndex, this.currentPageIndex);
    }

  }

  fetchUsersObservable(pageNumber: number, pageSize: number) {
    return this.http.post<UsersResponse>(
      API_ENDPOINTS.admin.fetchPaginatedUsers,
      {
        pageNumber,
        pageSize
      },
      {
        withCredentials: true
      }
    );
  }


  requestedPage: number = 1;


  selectOtherPage(pageNumber: any){
    if (!this.checkUnsavedModificationsOnUser()) {
      this.goToPage(pageNumber);
    } else {
      this.openPageChangeDialog(pageNumber, this.currentPageIndex);
    }
  }


  goToPage(page: number) {
    this.currentPageIndex = page;

    this.fetchUsersObservable(
      page,
      this.currentPageSize
    ).subscribe({
      next: response => {
        this.arrayUsers = response.users;
        this.totalUsers = response.totalElements;
        this.totalPages = response.totalPages;
        if (this.arrayUsers.length > 0) {
          this.selectUser(this.arrayUsers[0]);
          this.updateFullForm(this.arrayUsers[0]);
        }
      },
      error: error => console.error(error)
    });
    this.paginator.pageIndex = page;
  }

  updatePages(n: number) {
    this.pages = [];
    for (let i = 0; i < n; i++) {
      this.pages.push(i);
    }
  }


  onSelectionChange(row: UserNoRelations) {
    if (!this.checkUnsavedModificationsOnUser()) {
      this.selectedUser = row;
      this.updateFullForm(row);
    } else {
      this.openUnsavedDialog(row);
    }

  }

  checkUnsavedModificationsOnUser(): boolean {
    if (this.form.dirty) {
      return true;
    }
    return false;
  }

  updateFullForm(selected: UserNoRelations) {
    this.form.patchValue({
      id: selected.id,
      username: selected.username,
      role: selected.role,
      deleteDate: selected.deleteDate,
      email: selected.email,
    });
    this.inputDate.nativeElement.value = this.deleteDateFormatter(selected.deleteDate);
  }

  compareRoles(
    role1: RoleWithoutPermissions | null,
    role2: RoleWithoutPermissions | null
  ): boolean {
    return role1?.id === role2?.id;
  }

  openUnsavedDialog(selected: UserNoRelations): void {
    const dialogRef = this.confirmDialog.open(
      ConfirmationDialogComponent,
      {
        width: this.dialogOptions.width,
        height: this.dialogOptions.height,
        hasBackdrop: this.dialogOptions.hasBackdrop,
        disableClose: this.dialogOptions.disableClose,
        panelClass: ['confirmation-dialog', 'dialog'],
      }
    );

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        //user confirms he wants to leave
        this.updateFullForm(selected);
        this.selectedUser = selected;
        this.form.markAsPristine();
      }
    });
  }

  hasUnsavedChanges() {
    return this.form.dirty;
  }

  canLeavePage(): Observable<boolean> {

    if (!this.form.dirty) {
      return of(true);
    }

    const dialogRef = this.confirmDialog.open(
      ConfirmationDialogComponent,
      {
        width: this.dialogOptions.width,
        height: this.dialogOptions.height,
        hasBackdrop: this.dialogOptions.hasBackdrop,
        disableClose: this.dialogOptions.disableClose,
        panelClass: ['confirmation-dialog', 'dialog'],
      }
    );

    return dialogRef.afterClosed().pipe(
      tap(result => {
        if (result === true) {
          this.form.markAsPristine();
        }
      }),
      map(result => result === true)
    );
  }

  setDeleteDate() {
    let deletionDate: Date = new Date(Date.now());
    deletionDate.setDate(deletionDate.getDate() + 7);
    this.form.patchValue({ deleteDate: deletionDate });
    this.inputDate.nativeElement.value = this.deleteDateFormatter(deletionDate);
  }

  cancelDeletion() {
    this.form.patchValue({ deleteDate: null });
    this.inputDate.nativeElement.value = this.deleteDateFormatter(null);
  }

  completeProcedure() {
    let pageNumber: number = 0;
    of(null).pipe(
      concatMap(() => this.saveChangesObservable()),
      tap((response) => {
        pageNumber = response.page;
      }),
      concatMap(() => this.fetchUsersObservable(pageNumber, this.defaultPageSize)),
    ).subscribe({
      next: (response) => {
        this.arrayUsers = response.users;
        this.totalUsers = response.totalElements;
        this.totalPages = response.totalPages;
        this.updatePages(response.totalPages);
        this.snackBarService.showSuccessMessageSnackBar('User update successfull');
        this.selectedUser = this.arrayUsers.filter((usr) => usr.id === this.form.value.id)[0];
        this.form.markAsPristine();
      },
      error: (error) => {
        console.log(error);
        this.snackBarService.showErrorSnackBar(error);
      },
    })
  }

  saveChangesObservable(): Observable<any> {
    return this.http.patch(API_ENDPOINTS.admin.updateUser, { "user": this.form.value, "pageNumber": 3 }, { withCredentials: true });
  }

  cancelChanges(): void {
    let user = undefined;
    if (this.form.value.id) {
      user = this.arrayUsers.find((u) => u.id === this.form.value.id);
    }

    if (user) {
      this.updateFullForm(user);
      this.form.markAsPristine();
    }
  }

  openPageChangeDialog(futurePageIndex: number, previousIndex: number): void {
    const dialogRef = this.confirmDialog.open(
      ConfirmationDialogComponent,
      {
        width: this.dialogOptions.width,
        height: this.dialogOptions.height,
        hasBackdrop: this.dialogOptions.hasBackdrop,
        disableClose: this.dialogOptions.disableClose,
        panelClass: ['confirmation-dialog', 'dialog'],
      }
    );

    //this is required for the select page to work properly. Otherwise it will fail because at this point currentPageIndex is not updated to the select value. Therefore the code in the afterClosed will not make any change to the value from Angular's point of view and it will not update it back.
    this.currentPageIndex = futurePageIndex;
    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        //user confirms he wants to leave
        this.goToPage(futurePageIndex);
        this.form.markAsPristine();
      }
      if (result === false){ 
        this.paginator.pageIndex = previousIndex;
        this.currentPageIndex = previousIndex;
      }
    });
  }


}