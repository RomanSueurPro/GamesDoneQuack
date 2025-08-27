import { Component } from '@angular/core';
import { SessionComponent } from "./session/session.component";

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [SessionComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {

}
