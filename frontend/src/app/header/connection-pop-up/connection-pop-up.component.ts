import { Component, Inject, ViewChild, ElementRef } from '@angular/core';
import {MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,} from '@angular/material/dialog';
import { BackendService } from '../../services/backend.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms";



@Component({
  selector: 'app-connection-pop-up',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
    CommonModule,
    FormsModule,
    
],
  templateUrl: './connection-pop-up.component.html',
  styleUrls: ['./connection-pop-up.component.scss']
})
export class ConnectionPopUpComponent {
  constructor(
  public dialogRef: MatDialogRef<ConnectionPopUpComponent>,
  // @Inject(MAT_DIALOG_DATA) public data: DialogData,
  private backendService: BackendService,
  ) {}

  public isModeLogin: boolean = true;

  onNoClick(): void {
    this.dialogRef.close();
  }

  sendNameToBack(): void {
    this.backendService.sendRegisterRequest('JeanDeux', 'putois');
  }

  enterAccountCreationMode(): void {
    console.log("not implemented ");
  }

  @ViewChild('toggleButton') toggleButton!: ElementRef<HTMLButtonElement>;

  toggleModeLogin(): void{
    this.isModeLogin = (this.isModeLogin === true) ? false : true;
    this.toggleButton.nativeElement.innerText = this.isModeLogin ? 'Créer un compte' : 'Se connecter';
  }

  registerUsername:string = '';
  passwordUsername:string = '';
  registerNewAndApprovedUser(): void{
    const username = this.registerUsername;
    const password = this.passwordUsername;
    this.backendService.sendRegisterRequest(username, password);
  }
}
