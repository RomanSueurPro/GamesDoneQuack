import { Component, Input, ViewChild } from '@angular/core';
import { RoleAllFields } from '../../../models/RoleAllFields';
import { API_ENDPOINTS } from '../../../config/api-endpoints';
import { HttpClient } from '@angular/common/http';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { FormsModule } from "@angular/forms";
import { UserNoRelations } from '../../../models/UserNoRelations';
import { concatMap, forkJoin, pipe, tap } from 'rxjs';
import { UsersResponse } from '../../../models/UsersResponse';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';


@Component({
  selector: 'app-user-list',
  standalone: true,
  imports: [
    MatTableModule,
    FormsModule,
    MatPaginator,
    MatPaginatorModule
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.scss'
})
export class UserListComponent {

  constructor(
    private http: HttpClient,
  ) { }

  @Input()
  set active(value: boolean) {
    if (value) {
      this.loadDataObservable().subscribe({
        error: (error) => console.log(error.error.error)
      });
    }
  }

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  public arrayUsers: UserNoRelations[] = [];

  dataSource = new MatTableDataSource<UserNoRelations>();


  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  ngOnInit() {
    this.loadDataObservable().subscribe({
      error: (error) => console.log(error),
    });
  }


  loadDataObservable() {
    return this.fetchUsersObservable().pipe(
      tap(users => {
        this.arrayUsers = users;
        this.dataSource.data = users;
      })
    );
  }

  fetchUsersObservable() {
    return this.http.get<UserNoRelations[]>(API_ENDPOINTS.admin.fetchAllUsers, { withCredentials: true });
  }

  displayedColumns: string[] = ['id', 'username', 'email', 'role'];
  // dataSource = this.arrayUsers;

  searchInput: String = "";

  testload() {
    return this.fetch10UsersObservable().pipe(
      tap(response => {
        this.arrayUsers = [];
        this.arrayUsers = response.content;
        console.log(response);
        console.log(this.arrayUsers);
      })
    ).subscribe({
      error: (error) => console.log(error),
    });
  }

  fetch10UsersObservable() {
    return this.http.post<UsersResponse>(API_ENDPOINTS.admin.fetch10Users,
      {
        "pageNumber": 0,
        "pageSize": 3,
      },
      { withCredentials: true });
  }

  //useful for Search by Username for future selves <3
  testSearch() {
    console.log(this.searchInput);
  }

  onPageChange(event: PageEvent) {
    console.log(event);
    this.fetchUsers(event.pageIndex, event.pageSize);
  }

  fetchUsers(pageNumber: number, pageSize: number) {
    this.http.post<UsersResponse>(
      API_ENDPOINTS.admin.fetch10Users,
      {
        pageNumber,
        pageSize
      },
      {
        withCredentials: true
      }
    ).subscribe(response => {
      // this.dataSource.data = response.content;
      console.log(response);
      this.arrayUsers = [];
      this.arrayUsers = response.content;
    });
  }

}
