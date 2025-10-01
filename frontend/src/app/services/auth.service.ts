import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { map, switchMap, concatMap, catchError } from 'rxjs/operators';
import { CsrfService } from './csrf.service';
import { Observable, of } from 'rxjs';
import { AuthStateService } from './auth-state.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient, private csrfService: CsrfService, private authState: AuthStateService) { }


  checkLogin(){
    this.http.get('http://localhost:8080/api/me', {withCredentials: true}).subscribe({
      next: () => {
        console.log(" /api/me says logged in");
        this.authState.login();
        console.log("Signal value now:", this.authState.isLoggedIn());
      },
      error: () => {
        console.log(" /api/me says not logged in");
        this.authState.logout();
        console.log("Signal value now:", this.authState.isLoggedIn());
      }
    });
  }

  checkLoginObservable(): Observable<boolean> {
    return this.http.get('http://localhost:8080/api/me', { withCredentials: true }).pipe(
      // map to true/false for signal
      map(() => {
        this.authState.login();
        console.log("Signal value now:", this.authState.isLoggedIn());
        return true;
      }),
      // catch errors and set false
      catchError(err => {
        this.authState.logout();
        console.log("Signal value now:", this.authState.isLoggedIn());
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

  sendRegisterRequest(username: string, pass: string){
      const registerUrl: string = 'http://localhost:8080/register';
      let requestBody: URLSearchParams = new URLSearchParams();
      const csrfToken = this.getCSRFTokenFromCookies();

      requestBody.set('username', username);
      requestBody.set('password', pass);
      requestBody.set('_csrf', csrfToken || '');
      return this.http.post(registerUrl, requestBody,
          {withCredentials: true, headers: new HttpHeaders({
              'Content-Type': 'application/x-www-form-urlencoded',
          }),}
      );
  }

  sendLoginRequest(username: string, password: string){
        const loginUrl = 'http://localhost:8080/login';
        const testUserName = username;
        const testUserPassword = password;
        const csrfToken = this.getCSRFTokenFromCookies();

        const body = new URLSearchParams();
        body.set('username', testUserName);
        body.set('password', testUserPassword);
        body.set('_csrf', csrfToken || '');

        return this.http.post(loginUrl, body.toString(), {
            headers: new HttpHeaders({
                'Content-Type': 'application/x-www-form-urlencoded',
            }),
            withCredentials: true,
        });
    }






}
