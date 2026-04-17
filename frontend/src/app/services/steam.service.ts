import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../config/api-endpoints';

@Injectable({
    providedIn: 'root'
})

export class SteamService {
    constructor(private http: HttpClient){}

    getSteamData(): Observable<any> {
        return this.http.get(API_ENDPOINTS.steam.allData, {
            withCredentials: true,
        });
    }
}