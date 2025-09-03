import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})

export class KaamelottService {
    constructor(private http: HttpClient){}

    getKaamelottData(): Observable<any> {
        return this.http.get('http://localhost:8080/api/kaamelott/', {
            withCredentials: true,
        });
    }
}