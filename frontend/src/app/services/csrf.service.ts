import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class CsrfService {

  constructor(private http: HttpClient) { }



  loadUp(){
    this.http.get('http://localhost:8080/csrf', {withCredentials: true}).subscribe({
        next: () => console.log('Csrf load OK'),
        error: () => console.log('Csrf load did not work as intended (it is not OK)')  
    });
  }


  loadUpObservable(): Observable<any> {
    // Return the observable instead of subscribing here
    return this.http.get('http://localhost:8080/csrf', { withCredentials: true });
  }
}
