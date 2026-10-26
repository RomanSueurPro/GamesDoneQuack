import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../../config/api-endpoints';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {

  constructor(private http: HttpClient){}

  ngOnInit(){
    this.fetchUserInfoObservable().subscribe({
      next: () => console.log('success')
    });
  }

  fetchUserInfoObservable():Observable<any>{
    return this.http.get(API_ENDPOINTS.user.userInfo);
  }

}
