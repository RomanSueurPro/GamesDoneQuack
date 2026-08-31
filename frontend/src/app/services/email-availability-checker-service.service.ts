import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_ENDPOINTS } from '../config/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class EmailAvailabilityCheckerServiceService {

  constructor(private http : HttpClient) { }
  
    checkEmailAvailability(name: string){  
      return this.http.post(API_ENDPOINTS.auth.checkemail, name, {withCredentials: true});
    }
}
