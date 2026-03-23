import { Component } from '@angular/core';
import { Role } from '../../../models/Role';
import { catchError, map, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [],
  templateUrl: './role-list.component.html',
  styleUrl: './role-list.component.scss'
})
export class RoleListComponent {

  constructor(private http: HttpClient){}

  public roleList = {};

  ngOnInit(){
    this.loadRoles();

  }

  refreshObject(){
    this.loadRoles();
  }

  fetchRolesObservable(){
    return this.http.get('http://localhost:8080/admin/fetchroles', {withCredentials: true});
  }

  loadRoles(){
    this.fetchRolesObservable().subscribe({
      next: (result) => this.roleList = JSON.stringify(result),
      error: (error) => console.log(error)
    })
  }

}
