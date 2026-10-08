import { Injectable, signal, computed } from '@angular/core';
import { LoggedInUser } from '../models/LoggedInUser'; 
import { AdminRoleNameService } from './admin-role-name.service';

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  
  constructor(private adminRoleNameService: AdminRoleNameService){}

  user = signal<LoggedInUser | null>(null);
  readonly isLoggedIn = computed(() => this.user() !== null);
  readonly isAdmin = computed(() => this.user()?.role.name === this.adminRoleNameService.getRoleName());

  hasRole(role:string):boolean{
    if(!this.user()){
      return false;
    }
    if(this.user()?.role.name !== role){
      return false;
    }
    return true;
  }

  setUser(user: LoggedInUser){
    this.user.set(user);
  }

  clear(){
    this.user.set(null);
  }
}
