import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';



@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';
  response: string = 'empty';

  constructor(private http: HttpClient){}

    ngOnInit(): void{
      this.http.get('http://localhost:8080/home', { responseType: 'text'}).subscribe(
        data => {
          this.response = data;
        }
      );
    }

}
