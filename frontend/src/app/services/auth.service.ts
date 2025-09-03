import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { map, switchMap, concatMap, catchError } from 'rxjs/operators';
import { CsrfService } from './csrf.service';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private csrfService: CsrfService) { }

  isLoggedIn = signal(false);

  checkLogin(){
    this.http.get('http://localhost:8080/api/me', {withCredentials: true}).subscribe({
      next: () => {
        console.log(" /api/me says logged in");
        this.isLoggedIn.set(true);
        console.log("Signal value now:", this.isLoggedIn());
      },
      error: () => {
        console.log(" /api/me says not logged in");
        this.isLoggedIn.set(false);
        console.log("Signal value now:", this.isLoggedIn());
      }
    });
  }

  checkLoginObservable(): Observable<boolean> {
    return this.http.get('http://localhost:8080/api/me', { withCredentials: true }).pipe(
      // map to true/false for signal
      map(() => {
        this.isLoggedIn.set(true);
        console.log("Signal value now:", this.isLoggedIn());
        return true;
      }),
      // catch errors and set false
      catchError(err => {
        this.isLoggedIn.set(false);
        console.log("Signal value now:", this.isLoggedIn());
        return of(false);
      })
    );
}

  logout(){
    of(null).pipe(
      concatMap(() => this.checkCSRFObservable()),
      concatMap(() => this.logoutObservable()),
      concatMap(() => this.checkLoginObservable())
    ).subscribe({
      next: () => console.log('sure'),
      error: () => console.log('not sure')
    })
  };

  checkCSRFObservable(): Observable<void>{
    if(this.getCSRFTokenFromCookies() === null){
      return this.csrfService.loadUpObservable().pipe(
        switchMap(() => of(void 0))
      );
    }
    return of(void 0);
  }

  logoutObservable(): Observable<any>{
    const body = new URLSearchParams();
    const csrfToken = this.getCSRFTokenFromCookies();
    body.set('_csrf', csrfToken || '');

    return this.http.post('http://localhost:8080/logout', body, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      withCredentials: true
    });
  }

  getCSRFTokenFromCookies(): string | null{
    const match = document.cookie.match(new RegExp('(^| )' + 'XSRF-TOKEN' + '=([^;]+)'));
    //match returns an array
    //match[2] is the part after the = sign
    return match ? decodeURIComponent(match[2]) : null;
  }
}
