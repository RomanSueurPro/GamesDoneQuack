import { Component } from '@angular/core';
import { HttpClient, HttpClientModule} from '@angular/common/http';
import { KaamelottService } from '../../services/kaamelott.service';
import { NgIf } from '@angular/common';

import { BackendService } from '../../services/backend.service';
import { HeaderComponent } from "../../header/header.component";
import { AuthService } from '../../services/auth.service';
import { AuthStateService } from '../../services/auth-state.service';
import { AdminRoleNameService } from '../../services/admin-role-name.service';
import { API_ENDPOINTS } from '../../config/api-endpoints';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HttpClientModule, NgIf, HeaderComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  title = 'frontend';
    response: string = 'empty';
    messages: string[] = [];
    kaamelottData: string | undefined;
    adminRoleName: string | undefined;
  
    constructor(private http: HttpClient, private kaamelottService: KaamelottService, private backendService: BackendService, private authService: AuthService, public authState: AuthStateService, private adminRoleNameService: AdminRoleNameService, private router: Router){}
  
    ngOnInit(): void{
      //just checking we can reach backend
      this.http.get(API_ENDPOINTS.homePage.home, { responseType: 'text'}).subscribe(
        data => {
          this.response = data;
        }
      );
      // this.backendService.superLogMe();
      // console.log("!! superLogMe is activated for dev profile. Build is not viable for production. !!");
    }
  
    fetchKaamelottData(){
      this.kaamelottService.getKaamelottData().subscribe({
        next: (data) => this.kaamelottData = JSON.stringify(data),
        error: (err) => this.kaamelottData = 'Error fetching kaamelott data'
      });
    }
  
    logout(){
      this.authService.logout();
    }
  
    refreshCsrf(){
      this.backendService.fetchCsrf();
    }
  
  
    updateLoginStatus(){
      this.authState.isLoggedIn();
    }

    callRoleNameServiceTest(){
      this.adminRoleName = this.adminRoleNameService.getRoleName();
    }
    
    goAdminPage(){
      this.router.navigate(['admin']);
    }

    goProfilePage(){
      console.log("profile page");
    }
}
