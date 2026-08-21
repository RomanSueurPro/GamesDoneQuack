import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { map, switchMap, concatMap, catchError } from 'rxjs/operators';
import { CsrfService } from './csrf.service';
import { Observable, of, tap } from 'rxjs';
import { AuthStateService } from './auth-state.service';
import { User } from '../models/User';
import { API_ENDPOINTS } from '../config/api-endpoints';
import { Router } from '@angular/router';
import { FormGroup } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient,
    private csrfService: CsrfService,
    private authState: AuthStateService,
    private router: Router) { }


  loadUser(){
    return this.http.get<User>(API_ENDPOINTS.auth.me,
    {withCredentials: true});
  }

  refreshUser(){
    this.loadUser().subscribe({
      next: (user: any) => {
        if(user){
          this.authState.setUser(user);
        }else{
          this.authState.clear();
        }
      },
      error: () => {
        this.authState.clear();
        console.error("refreshUser() went to error");
      }
    });
  }

  checkLoginObservable(): Observable<boolean> {
    return this.loadUser().pipe(
      // map to true/false for signal
      tap((user) => {
        this.authState.user.set(user);
      }),
      map(() => true),
      // catch errors and set false
      catchError(err => {
        this.authState.clear();
        return of(false);
      })
    );
  }

  logout(){
    of(null).pipe(
      concatMap(() => this.checkCSRFObservable()),
      concatMap(() => this.logoutObservable()),
      concatMap(() => this.checkLoginObservable()),
      concatMap(() => this.router.navigateByUrl('/')),
      concatMap(() => this.csrfService.loadUpObservable()),

    ).subscribe({
      next: () => {
        console.log('Logout successful');
        
      },
      error: (error) => {
        console.log(error);
      }
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
    

    return this.http.post(API_ENDPOINTS.auth.logout,{
      withCredentials: true
    });
  }

  getCSRFTokenFromCookies(): string | null{
    const match = document.cookie.match(new RegExp('(^| )' + 'XSRF-TOKEN' + '=([^;]+)'));
    //match returns an array
    //match[2] is the part after the = sign
    return match ? decodeURIComponent(match[2]) : null;
  }

  sendRegisterRequest(group: FormGroup){
      const registerUrl: string = API_ENDPOINTS.auth.register;
      return this.http.post(registerUrl, group.value,
          {withCredentials: true}
      );
  }

  sendLoginRequest(group: FormGroup):Observable<any>{
    const loginUrl = API_ENDPOINTS.auth.login;
    
    return this.http.post(loginUrl, group.value, {withCredentials: true});
  }
}
