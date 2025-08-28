import { Component, Inject } from '@angular/core';
import {MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,} from '@angular/material/dialog';

@Component({
  selector: 'app-connection-pop-up',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,],
  templateUrl: './connection-pop-up.component.html',
  styleUrl: './connection-pop-up.component.scss'
})
export class ConnectionPopUpComponent {
  constructor(
  public dialogRef: MatDialogRef<ConnectionPopUpComponent>,
  // @Inject(MAT_DIALOG_DATA) public data: DialogData,
  ) {}

onNoClick(): void {
  this.dialogRef.close();
}

  
}
