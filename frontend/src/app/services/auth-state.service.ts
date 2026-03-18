import { Injectable, signal, computed } from '@angular/core';
import { User } from '../models/User'; 

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  
  user = signal<User | null>(null);
  readonly isLoggedIn = computed(() => this.user() !== null);

  readonly isAdmin = computed(() => this.user()?.roleName === 'ROLE_ADMIN');

  hasRole(role:string):boolean{
    if(!this.user()){
      return false;
    }
    if(this.user()?.roleName !== role){
      return false;
    }
    return true;
  }

  setUser(user: User){
    this.user.set(user);
  }

  clear(){
    this.user.set(null);
  }
}
