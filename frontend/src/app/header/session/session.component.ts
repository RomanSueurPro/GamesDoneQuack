import {Component, Inject} from '@angular/core';
import { MatDialog} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { ConnectionPopUpComponent } from '../connection-pop-up/connection-pop-up.component';
import { AuthStateService } from '../../services/auth-state.service';
import { NgTemplateOutlet } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import {MatMenuModule} from '@angular/material/menu';


@Component({
  selector: 'app-session',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, FormsModule, MatButtonModule,
    NgTemplateOutlet, MatMenuModule],
  templateUrl: './session.component.html',
  styleUrls: ['./session.component.scss']
})
export class SessionComponent {

constructor(public dialog: MatDialog, public authState: AuthStateService, public authService: AuthService){}

  showPopup(): void{
    let dialogRef = this.dialog.open(ConnectionPopUpComponent, {
      data: {name: 'Arthur'},
      height: '200px',
      width: '300px',
      disableClose: false,
      hasBackdrop: true,
      panelClass: 'connection-pop-up',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
    });
  }

  logout(){
    this.authService.logout();
  }

}


