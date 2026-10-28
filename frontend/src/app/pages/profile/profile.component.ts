import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Observable } from 'rxjs';
import { API_ENDPOINTS } from '../../config/api-endpoints';
import { AuthStateService } from '../../services/auth-state.service';
import { UserProfile } from '../../models/UserProfile';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {

  constructor(private http: HttpClient, private authStateService: AuthStateService) { }

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  id = 0;

  ngOnInit() {
    let username: String = "";
    this.route.params.subscribe(params => {
      username = params['username'];
      let id = history.state.userId;
      if (id !== null && id !== undefined) {
        this.fetchUserInfoObservableById(history.state.userId).subscribe({
          error: (error) => console.log(error)
        });

      } else {
        this.fetchUserInfoObservableByUsername(username).subscribe({
          error: (error) => console.log(error)
        });

      }
    });

  }

  fetchUserInfoObservableById(id: number): Observable<any> {
    return this.http.post<UserProfile>(
      API_ENDPOINTS.user.userInfoById, id, { withCredentials: true });
  }

  fetchUserInfoObservableByUsername(username: String): Observable<any> {
    return this.http.post<UserProfile>(
      API_ENDPOINTS.user.userInfoByusername, username, { withCredentials: true });
  }

}
