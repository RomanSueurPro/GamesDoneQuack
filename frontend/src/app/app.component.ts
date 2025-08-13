import { HttpClient, HttpClientModule} from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { KaamelottService } from './kaamelott.service';
import { NgIf } from '@angular/common';
import { BackendService } from './backend-service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HttpClientModule, NgIf],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})



export class AppComponent {
  title = 'frontend';
  response: string = 'empty';

  messages: string[] = [];
  kaamelottData: string | undefined;


  constructor(private http: HttpClient, private kaamelottService: KaamelottService, private backendService: BackendService){}

    ngOnInit(): void{
      this.http.get('http://localhost:8080/home', { responseType: 'text'}).subscribe(
        data => {
          this.response = data;
        }
      );
      this.refreshCsrf();
    }


    fetchKaamelottData(){
      this.kaamelottService.getKaamelottData().subscribe({
        next: (data) => this.kaamelottData = JSON.stringify(data),
        error: (err) => this.kaamelottData = 'Error fetching kaamelott data'
      });
    }



    autoLogin(){
      this.backendService.autologinService();
    }


    refreshCsrf(){
      this.backendService.getRequest();
    }


    visitLogin(){
      this.backendService.visitLogin();
    }

}
