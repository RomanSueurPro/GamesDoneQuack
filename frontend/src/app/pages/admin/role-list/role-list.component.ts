import { Component } from '@angular/core';
import { RoleWithoutPermissions } from '../../../models/RoleWithoutPermissions';
import { catchError, map, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { RoleAllFields } from '../../../models/RoleAllFields';
import { MatListModule } from '@angular/material/list';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';

@Component({
  selector: 'app-role-list',
  standalone: true,
  imports: [MatListModule, FormsModule, ReactiveFormsModule],
  templateUrl: './role-list.component.html',
  styleUrl: './role-list.component.scss'
})
export class RoleListComponent {

  constructor(private http: HttpClient, ){
    this.form = new FormGroup({
      roles: this.rolesControl,
    });
  }

  form: FormGroup;
  public roleList = {};
  public arrayRoles: RoleAllFields[] = [];
  rolesControl = new FormControl();

  ngOnInit(){
    this.loadRoles();

  }

  refreshObject(){
    this.loadRoles();
  }

  fetchRolesObservable(){
    return this.http.get<RoleAllFields[]>('http://localhost:8080/admin/fetchroles', {withCredentials: true});
  }

  loadRoles(){
    this.fetchRolesObservable().subscribe({
      next: (result) => 
        {
          this.roleList = JSON.stringify(result);
          this.arrayRoles = result;
        },
      error: (error) => console.log(error)
    })
  }


  
}


