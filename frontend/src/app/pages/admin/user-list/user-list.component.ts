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
import { Ban } from '../../../models/Ban';
import { AuthService } from '../../../services/auth.service';
import { AuthStateService } from '../../../services/auth-state.service';
import { LoggedInUser } from '../../../models/LoggedInUser';


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
  defaultPageSize: number = 10;
  currentPageIndex: number = 0;
  currentPageSize: number = 10;
  totalPages: number = 0;
  pages: number[] = [];

  displayedColumns: string[] = ['username', 'role', 'deleteDate'];
  totalUsers: number = 0;

  searchInput: String = "";

  selectedUser: UserNoRelations | null = null;
  selectedRole: RoleWithoutPermissions | null = null;
  arrayRoles: RoleWithoutPermissions[] = [];
  BanForUser: Ban | null = null;
  newBanDuration: number = 0;
  newBanReason: string = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild('inputDate') inputDate!: ElementRef;
  @ViewChild('pageSelect') pageSelect!: ElementRef;

  constructor(
    private http: HttpClient, private confirmDialog: MatDialog, private datepipe: DatePipe,
    private snackBarService: SnackbarService, private authStateService: AuthStateService
  ) { }

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
    this.fetchBanObservable(user).subscribe({
      next: (ban) => {
        this.BanForUser = ban;
        console.log(ban);
      }
    })
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
          this.selectUser(firstUser);
          this.updateFullForm(firstUser);
        }
      })
    );
  }

  fetchRolesObservable() {
    return this.http.get<RoleWithoutPermissions[]>(API_ENDPOINTS.admin.fetchAllRolesNoPermissionField, { withCredentials: true });
  }

  //useful for Search by Username for future selves <3
  userSearch(pagenumber: number) {
    this.searchUsersByUsernameObservable(pagenumber, this.currentPageSize).subscribe({
      next: (response) => {
        console.log(response);
        this.arrayUsers = response.users;
        this.totalUsers = response.totalElements;
        this.totalPages = response.totalPages;
        this.updatePages(response.totalPages);
        if (this.arrayUsers.length > 0) {
          this.selectUser(this.arrayUsers[0]);
          this.updateFullForm(this.arrayUsers[0]);
        }
      },
    });

  }

  searchUsersByUsernameObservable(pageNumber: number, pageSize: number) {
    return this.http.post<UsersResponse>(
      API_ENDPOINTS.admin.searchUserAdmin,
      {
        input: this.searchInput,
        pageable: {
          pageNumber: pageNumber,
          pageSize: pageSize,
        }
      },
      {
        withCredentials: true
      }
    );
  }


  onPageChange(event: PageEvent) {
    if (!this.checkUnsavedModificationsOnUser()) {
      this.goToPage(event.pageIndex, event.pageSize);
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

  selectOtherPage(pageNumber: any) {
    if (!this.checkUnsavedModificationsOnUser()) {
      this.goToPage(pageNumber, this.currentPageSize);
    } else {
      this.openPageChangeDialog(pageNumber, this.currentPageIndex);
    }
  }


  goToPage(page: number, pageSize: number) {
    this.currentPageIndex = page;
    this.currentPageSize = pageSize;

    if (this.searchInput === '') {
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
    } else {
      this.userSearch(page);
    }

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
      this.selectUser(row);
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
        this.goToPage(futurePageIndex, this.currentPageSize);
        this.form.markAsPristine();
      }
      if (result === false) {
        this.paginator.pageIndex = previousIndex;
        this.currentPageIndex = previousIndex;
      }
    });
  }

  fetchBanObservable(user: UserNoRelations): Observable<Ban> {
    return this.http.post<Ban>(API_ENDPOINTS.admin.fetchUserLastBan, user, { withCredentials: true });
  }

  unbanUserObservable(user: UserNoRelations): Observable<any> {
    return this.http.post(API_ENDPOINTS.admin.unbanUser, user, { withCredentials: true });
  }

  unbanSelectedUser(): void {
    if (this.selectedUser !== null) {
      this.unbanUserObservable(this.selectedUser).subscribe({

      });
    }

  }

  banUserObservable(ban: Ban): Observable<any> {
    return this.http.post(API_ENDPOINTS.admin.banUser, ban, { withCredentials: true });
  }

  banSelectedUser(): void {
    const user: LoggedInUser | null = this.authStateService.user();
    const startDate = new Date(Date.now());
    const endDate = new Date(Date.now());
    endDate.setDate(endDate.getDate() + this.newBanDuration);

    //Plus 2 hours to adjust for GMT
    //TODO : handle GMT properly
    startDate.setHours(2, 0, 0, 0);
    if (this.selectedUser !== null && user !== null) {
      const ban: Ban = {
        id: -1,
        user: this.selectedUser,
        moderator: user,
        startDate: startDate,
        endDate: endDate,
        reason: this.newBanReason,
      }

      this.banUserObservable(ban).subscribe({
        next: () => { },
        error: (error) => console.log(error)
      })
    }
  }

}