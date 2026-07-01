import {Component} from '@angular/core';
import { MatDialog} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { ConnectionPopUpComponent } from '../connection-pop-up/connection-pop-up.component';
import { AuthStateService } from '../../services/auth-state.service';
import { NgTemplateOutlet, Location } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import {MatMenuModule} from '@angular/material/menu';
import { Router, RouterLink, RouterLinkWithHref, RouterOutlet } from "@angular/router";


@Component({
  selector: 'app-session',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, FormsModule, MatButtonModule,
    NgTemplateOutlet, MatMenuModule, RouterLink, RouterLinkWithHref,
  ],
  templateUrl: './session.component.html',
  styleUrls: ['./session.component.scss']
})
export class SessionComponent {

constructor(public dialog: MatDialog, public authState: AuthStateService, public authService: AuthService,
  private router: Router, private location: Location
){}

  showPopup(): void{
    // this.router.navigate(['/login'], {skipLocationChange: true});
    this.location.go('/login');
    const dialogRef = this.dialog.open(ConnectionPopUpComponent, {
      disableClose: false,
      hasBackdrop: true,
      panelClass: 'dialog',
      data: {
        loginMode: true
      }
    });

    dialogRef.afterClosed().subscribe({
      next: () => this.location.go('/')
     });
  }

  logout(){
    this.authService.logout();
  }

}


