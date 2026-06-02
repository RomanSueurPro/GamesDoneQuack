import { Component, inject } from '@angular/core';
import { SessionComponent } from "./session/session.component";
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [SessionComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

  router = inject(Router);

  goHome(){
    this.router.navigateByUrl("/");
  }
}
