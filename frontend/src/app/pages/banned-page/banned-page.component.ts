import { Component } from '@angular/core';
import { HeaderComponent } from '../../header/header.component';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-banned-page',
  standalone: true,
  imports: [HeaderComponent],
  templateUrl: './banned-page.component.html',
  styleUrl: './banned-page.component.scss'
})
export class BannedPageComponent {

  constructor(private auth: AuthService) {}

  unbanDate: Date = new Date();

  ngOnInit() {
    this.auth.checkLoginObservable().subscribe();
    this.auth.loadUser().subscribe({
      next: (response) => this.unbanDate = response.unbanDate
    })
  }

  private monthNames = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  ];

  private dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

  unbanDateFormatter(deleteDate: Date | null): String {
    let dateString: String = '';
    if (deleteDate === null) {
      dateString = "forever";
    } else {
      const date = new Date(deleteDate);

      dateString = this.dayNames[date.getDay()] + ", " + date.getDate() + this.properSuffix(date.getDate()) + " of " + this.monthNames[(date.getMonth())] + " " + date.getFullYear();
    }

    return dateString;
  }

  properSuffix(num: number): string {
    if (num % 10 === 1) {
      return "st";
    } else if (num % 10 === 2) {
      return "nd";
    } else if (num % 10 === 3) {
      return "rd";
    } else {
      return "th";
    }

  }

}
