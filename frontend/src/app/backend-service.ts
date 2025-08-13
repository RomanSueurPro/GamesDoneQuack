import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({
    providedIn : 'root'
})

export class BackendService {

    constructor(private http: HttpClient){}

    autologinService(){
        const loginUrl = 'http://localhost:8080/login';
        const testUserName = 'user';
        const testUserPassword = 'usertest@12345';
        const csrfToken = this.getCSRFTokenFromCookies('XSRF-TOKEN');

        const body = new URLSearchParams();
        body.set('username', testUserName);
        body.set('password', testUserPassword);
        body.set('_csrf', csrfToken || '');

        this.http.post(loginUrl, body.toString(), {
            headers: new HttpHeaders({
                'Content-Type': 'application/x-www-form-urlencoded',
            }),
            withCredentials: true,
        }).subscribe({
            next: () => console.log('yes'),
            error: () => console.log('no'),
        });
    }

    getCSRFTokenFromCookies(name: string): string | null{
        const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        //match returns an array
        //match[2] is the part after the = sign
        return match ? decodeURIComponent(match[2]) : null;
    }

    getRequest(){
        this.http.get('http://localhost:8080/csrf', {withCredentials: true}).subscribe({
            next: () => console.log('oui'),
            error: () => console.log('error. error. red alert')  
        });
    }

    visitLogin(){
        this.http.get('http://localhost:8080/login', {withCredentials: true}).subscribe({
            next: () => console.log('oui'),
            error: () => console.log('error. error. red alert')  
        });
    }

}