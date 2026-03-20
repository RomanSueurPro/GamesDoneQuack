import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable} from 'rxjs';
import { Role } from '../models/Role';

@Injectable({
  providedIn: 'root'
})
export class AdminRoleNameService {

  constructor(private http: HttpClient) { }

  adminRoleName = signal<string>("");

  fetchAdminNameObservable():Observable<void>{
    return this.http.get<Role>('http://localhost:8080/adminrolename', 
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
