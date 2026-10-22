import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../config/api-endpoints';


@Injectable({
  providedIn: 'root'
})
export class CsrfService {

  constructor(private http: HttpClient) { }



  loadUp(){
    this.http.get(API_ENDPOINTS.auth.csrf, {withCredentials: true}).subscribe({      
        error: (error) => console.log(error)  
    });
  }


  loadUpObservable(): Observable<any> {
    // Return the observable instead of subscribing here
    return this.http.get(API_ENDPOINTS.auth.csrf, { withCredentials: true });
  }
}
