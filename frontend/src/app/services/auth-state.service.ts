import { Injectable, signal, computed } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {

  readonly isLoggedIn = signal(false);

  login()  { this.isLoggedIn.set(true); }
  logout() { this.isLoggedIn.set(false); }
  toggle() { this.isLoggedIn.update(v => !v); }
}
