import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable} from 'rxjs';
import { AdminRoleName } from '../models/adminRoleName';

@Injectable({
  providedIn: 'root'
})
export class AdminRoleNameService {

  constructor(private http: HttpClient) { }

  roleName = signal<string>("");

  fetchAdminNameObservable():Observable<void>{
    return this.http.get<AdminRoleName>('http://localhost:8080/adminrolename', 
      {withCredentials: true}).pipe(
    
      map(result => this.setRoleName(result.roleName))
    );
    
  }
 
  setRoleName(name:string){
    this.roleName.set(name);
  }

  testRoleName(){
    console.log(this.roleName());
  }
  
  getRoleName(){
    return this.roleName();
  }
}
