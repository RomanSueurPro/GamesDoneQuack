import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from "@angular/router";

@Component({
  selector: 'app-profile-component',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './profile-component.component.html',
  styleUrl: './profile-component.component.scss'
})
export class ProfileComponent {

}
