import {Component, Inject} from '@angular/core';
import {
  MatDialog,
  
} from '@angular/material/dialog';
import {MatButtonModule} from '@angular/material/button';
import {FormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import { ConnectionPopUpComponent } from '../connection-pop-up/connection-pop-up.component';

@Component({
  selector: 'app-session',
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, FormsModule, MatButtonModule],
  templateUrl: './session.component.html',
  styleUrls: ['./session.component.scss']
})
export class SessionComponent {

constructor(public dialog: MatDialog){}

  showPopup(): void{
    let dialogRef = this.dialog.open(ConnectionPopUpComponent, {
      data: {name: 'Arthur'},
      height: '400px',
      width: '600px',
      disableClose: false,
      hasBackdrop: true,
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
    });
  }
}


