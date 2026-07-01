import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_ENDPOINTS } from '../config/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class UsernameAvailabilityCheckerService {

  constructor(private http : HttpClient) { }

  checkUserNameAvailability(name: string){  
    return this.http.post(API_ENDPOINTS.auth.checkusername, name, {withCredentials: true});
  }
}
