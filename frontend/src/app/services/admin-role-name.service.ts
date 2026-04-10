import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable} from 'rxjs';
import { RoleWithoutPermissions } from '../models/RoleWithoutPermissions';

@Injectable({
  providedIn: 'root'
})
export class AdminRoleNameService {

  constructor(private http: HttpClient) { }

  adminRoleName = signal<string>("");

  fetchAdminNameObservable():Observable<void>{
    return this.http.get<RoleWithoutPermissions>('http://localhost:8080/adminrolename', 
      {withCredentials: true}).pipe(
    
      map(result => this.setRoleName(result.name))
    );
  }
 
  setRoleName(name:string){
    this.adminRoleName.set(name);
  }

  testRoleName(){
    console.log(this.adminRoleName());
  }
  
  getRoleName(){
    return this.adminRoleName();
  }
}
