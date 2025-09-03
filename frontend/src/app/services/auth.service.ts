import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  isLoggedIn = signal(false);


  checkLogin(){
    this.http.get('http://localhost:8080/api/me', {withCredentials: true}).subscribe({
      next: () => this.isLoggedIn.set(true),
      error: () => this.isLoggedIn.set(false)
    });
  }

  logout(){

    const body = new URLSearchParams();
    const csrfToken = this.getCSRFTokenFromCookies('XSRF-TOKEN');
    body.set('_csrf', csrfToken || '');


    this.http.post('http://localhost:8080/logout', body, {headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                withCredentials: true}).subscribe(
      () => {this.isLoggedIn.set(false);}
    )
  };

  getCSRFTokenFromCookies(name: string): string | null{
        const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        //match returns an array
        //match[2] is the part after the = sign
        return match ? decodeURIComponent(match[2]) : null;
    }

}
