import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterLinkActive } from '@angular/router';
import { map, Observable, switchMap } from 'rxjs';
import { API_ENDPOINTS } from '../../config/api-endpoints';
import { AuthStateService } from '../../services/auth-state.service';
import { UserProfile } from '../../models/UserProfile';
import { HeaderComponent } from '../../header/header.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    RouterLink,
    RouterLinkActive,
    HeaderComponent,
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent {

  constructor(private http: HttpClient, private authStateService: AuthStateService, private route: ActivatedRoute, private router: Router) { }


  userProfile: UserProfile | null = null;
  isLoading = true;
  readonly currentUser = this.authStateService.user;


  ngOnInit(): void {
    this.route.paramMap.pipe(
      map(params => params.get('username')!),
      switchMap(username => {
        const userId = history.state.userId;
        this.isLoading = true;
        this.userProfile = null;

        return userId != null
          ? this.fetchUserInfoObservableById(userId)
          : this.fetchUserInfoObservableByUsername(username);
      })
    ).subscribe({
      next: profile => {
        this.userProfile = profile;
        this.isLoading = false;
      },
      error: error => {
        console.error(error);
        this.userProfile = null;
        this.isLoading = false;
      }
    });
  }



  fetchUserInfoObservableById(id: number): Observable<any> {
    return this.http.post<UserProfile>(
      API_ENDPOINTS.user.userInfoById, id, { withCredentials: true });
  }

  fetchUserInfoObservableByUsername(username: string): Observable<any> {
    return this.http.post<UserProfile>(
      API_ENDPOINTS.user.userInfoByusername, username, { withCredentials: true });
  }

  navigateToEdit() {
    this.router.navigate(['editprofile']);
  }

}
