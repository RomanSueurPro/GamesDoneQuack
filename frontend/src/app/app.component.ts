import { HttpClient, HttpClientModule} from '@angular/common/http';
import { Component } from '@angular/core';
import { SteamService } from './services/steam.service';
import { KaamelottService } from './services/kaamelott.service';
import { NgIf } from '@angular/common';
import { BackendService } from './services/backend.service';
import { HeaderComponent } from "./header/header.component";
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [HttpClientModule, NgIf, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})

export class AppComponent {
  title = 'frontend';
  response: string = 'empty';
  messages: string[] = [];
  kaamelottData: string | undefined;
  steamData: string | undefined;

  constructor(private http: HttpClient, private kaamelottService: KaamelottService, private steamService: SteamService, private backendService: BackendService, private authService: AuthService){}

  isLoggedIn = this.authService.isLoggedIn;

  ngOnInit(): void{
    this.http.get('http://localhost:8080/home', { responseType: 'text'}).subscribe(
      data => {
        this.response = data;
      }
    );
    this.refreshCsrf();
    this.authService.checkLogin();
  }

  fetchKaamelottData(){
    this.kaamelottService.getKaamelottData().subscribe({
      next: (data) => this.kaamelottData = JSON.stringify(data),
      error: (err) => this.kaamelottData = 'Error fetching kaamelott data'
    });
  }

  fetchSteamData(){
    this.steamService.getSteamData().subscribe({
      next: (data) => this.kaamelottData = JSON.stringify(data),
      error: (err) => this.kaamelottData = 'Error fetching steam data'
    });
  }

  autoLogin(){
    this.backendService.autologinService();
  }

  logout(){
    this.authService.logout();
  }

  refreshCsrf(){
    this.backendService.getRequest();
  }

  visitLogin(){
    this.backendService.visitLogin();
  }
}
