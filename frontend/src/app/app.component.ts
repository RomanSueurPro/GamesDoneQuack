import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { SteamService } from './steam.service';
import { NgIf } from '@angular/common';


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
  steamData: string | undefined;


  constructor(private http: HttpClient, private steamService: SteamService){}

    ngOnInit(): void{
      this.http.get('http://localhost:8080/home', { responseType: 'text'}).subscribe(
        data => {
          this.response = data;
        }
      );
    }


    fetchSteamData(){
      this.steamService.getSteamData().subscribe({
        next: (data) => this.steamData = JSON.stringify(data),
        error: (err) => this.steamData = 'Error fetching steam data'
      });
    }

}
