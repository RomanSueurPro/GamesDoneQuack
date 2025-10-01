import { Component, Inject, ViewChild, ElementRef } from '@angular/core';
import {
  MatDialogRef,
  } from '@angular/material/dialog';
import { BackendService } from '../../services/backend.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from "@angular/forms";
import { map, switchMap, concatMap, catchError } from 'rxjs/operators';
import { Observable, of, tap } from 'rxjs';
import { LoadingDotsComponent } from '../../animations/loading-dots/loading-dots.component';
import { modeSwitchAnimation } from './connection-pop-up-animation';

@Component({
  selector: 'app-connection-pop-up',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LoadingDotsComponent
],
  templateUrl: './connection-pop-up.component.html',
  styleUrls: ['./connection-pop-up.component.scss'],
  animations: [modeSwitchAnimation],
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

  @ViewChild('toggleButton') toggleButton!: ElementRef<HTMLButtonElement>;

  toggleModeLogin(): void{
    this.isModeLogin = (this.isModeLogin === true) ? false : true;
    this.toggleButton.nativeElement.innerText = this.isModeLogin ? 'Créer un compte' : 'Se connecter';
  }

  registerUsername:string = '';
  passwordUsername:string = '';
  registerNewAndApprovedUser(){
    const username = this.registerUsername;
    const password = this.passwordUsername;
    //this.backendService.sendRegisterRequest(username, password);
    return this.backendService.sendRegisterRequestFromAuth(username, password);
  }

  loginUsername:string = '';
  loginPassword:string = '';
  tryLogin(){
    const username = this.loginUsername;
    const password = this.loginPassword;
    return this.backendService.sendLoginRequestFromAuth(username, password);
  }
  //TODO pipe it up : 1 start animation 2 trylogin 3 stop animation close popup


  isLoading = false;
  buttonText = 'Start Loading';
  setLoading(state: boolean) {
    this.isLoading = state;
    this.buttonText = state ? 'Stop Loading' : 'Start Loading';
  }   

  toggleLoading(){
    this.isLoading = !this.isLoading;
    this.buttonText = this.isLoading ? 'Stop Loading' : 'Start Loading'; 
  }

  tryLogin2() {
    of(null).pipe(
          tap(() => this.setLoading(true)),
          concatMap(() => this.tryLogin()),
          tap(() => this.setLoading(false)),
          concatMap( () => this.backendService.checkLoginBackendObservable())
    ).subscribe({
      next: (response) => {
        console.log('Login success', response);
        this.backendService.getRequest();
        this.onNoClick();
      },
      error: (err) => {
        console.log('Login failed', err);
        this.setLoading(false);
      }
    });
  }


  tryRegister2() {
    of(null).pipe(
          tap(() => this.setLoading(true)),
          concatMap(() => this.registerNewAndApprovedUser()),
          tap(() => this.setLoading(false))
    ).subscribe({
      next: (response) => {
        console.log('Successfully registered', response);
        this.backendService.getRequest();
        this.onNoClick();
      },
      error: (err) => {
        console.log('Register error', err);
        this.setLoading(false);
      }
    });
  }


}
