import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AuthService } from "./auth.service";
import { CsrfService } from "./csrf.service";
import { API_ENDPOINTS } from "../config/api-endpoints";
import { FormGroup } from "@angular/forms";

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
    fetchCsrf(){
        this.csrfService.loadUp();
    }

    sendRegisterRequestFromAuth(group: FormGroup){
        return this.auth.sendRegisterRequest(group);
    }

    sendLoginRequestFromAuth(group : FormGroup){
        return this.auth.sendLoginRequest(group);
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