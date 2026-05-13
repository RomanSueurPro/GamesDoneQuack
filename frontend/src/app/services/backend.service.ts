import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AuthService } from "./auth.service";
import { CsrfService } from "./csrf.service";
import { API_ENDPOINTS } from "../config/api-endpoints";

@Injectable({
    providedIn : 'root'
})

export class BackendService {

    constructor(private http: HttpClient, private auth: AuthService, private csrfService: CsrfService){}

    getCSRFTokenFromCookies(name: string): string | null{
        const match = document.cookie.match(new RegExp('(^| )' + name + '=([^;]+)'));
        //match returns an array
        //match[2] is the part after the = sign
        return match ? decodeURIComponent(match[2]) : null;
    }

    //load csrf
    getRequest(){
        this.csrfService.loadUp();
    }

    visitLogin(){
        this.http.get(API_ENDPOINTS.auth.login, {withCredentials: true}).subscribe({
            next: () => console.log('oui'),
            error: () => console.log('error. error. red alert')  
        });
    }

    sendRegisterRequest(username: string, pass: string): void{
        const registerUrl: string = API_ENDPOINTS.auth.register;
        let requestBody: URLSearchParams = new URLSearchParams();
        const csrfToken = this.getCSRFTokenFromCookies('XSRF-TOKEN');

        requestBody.set('username', username);
        requestBody.set('password', pass);
        requestBody.set('_csrf', csrfToken || '');
        this.http.post(registerUrl, requestBody,
            {withCredentials: true, headers: new HttpHeaders({
                'Content-Type': 'application/x-www-form-urlencoded',
            }),}
         ).subscribe({
            next: () => console.log('request was sent'),
            error: () => console.log('error was occured yes')
        });
    }

    sendRegisterRequestFromAuth(username: string, pass: string){
        return this.auth.sendRegisterRequest(username, pass);
    }

    sendLoginRequestFromAuth(username: string, pass: string){
        return this.auth.sendLoginRequest(username, pass);
    }

    checkLoginBackendObservable(){
        return this.auth.checkLoginObservable();
    }

    superLogMe(){
        this.http.get(API_ENDPOINTS.homePage.superadmin, {
            withCredentials: true,
        }).subscribe({
            next: () => console.log("superuser online"),
            error: (error) => console.log(error),
        });
    }

}