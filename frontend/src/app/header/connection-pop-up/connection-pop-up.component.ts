import { Component, Inject } from '@angular/core';
import {MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,} from '@angular/material/dialog';
import { BackendService } from '../../services/backend.service';


@Component({
  selector: 'app-connection-pop-up',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,],
  templateUrl: './connection-pop-up.component.html',
  styleUrls: ['./connection-pop-up.component.scss']
})
export class ConnectionPopUpComponent {
  constructor(
  public dialogRef: MatDialogRef<ConnectionPopUpComponent>,
  // @Inject(MAT_DIALOG_DATA) public data: DialogData,
  private backendService: BackendService,
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  sendNameToBack(): void {
    this.backendService.sendRegisterRequest('Jean', 'putois');
  }
}
