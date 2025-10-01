import { Injectable, signal, computed } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {

  readonly isLoggedIn = signal(false);
  user = signal<{username: string} | null>(null);

  login()  { this.isLoggedIn.set(true); }
  logout() {
    this.isLoggedIn.set(false); 
    this.user.set(null);
  }
  toggle() { this.isLoggedIn.update(v => !v); }
}
