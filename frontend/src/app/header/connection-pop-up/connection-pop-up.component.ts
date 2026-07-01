import { Component, Inject, ViewChild, ElementRef } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  } from '@angular/material/dialog';
import { BackendService } from '../../services/backend.service';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule, Validators, ReactiveFormsModule } from "@angular/forms";
import { concatMap } from 'rxjs/operators';
import { of, tap } from 'rxjs';
import { LoadingDotsComponent } from '../../animations/loading-dots/loading-dots.component';
import { modeSwitchAnimation } from './connection-pop-up-animation';
import { UsernameAvailabilityCheckerService } from '../../services/username-availability-checker.service';

export interface DialogData {
  loginMode: boolean;
}

@Component({
  selector: 'app-connection-pop-up',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    LoadingDotsComponent,
    ReactiveFormsModule
],
  templateUrl: './connection-pop-up.component.html',
  styleUrls: ['./connection-pop-up.component.scss'],
  animations: [modeSwitchAnimation],
})


export class ConnectionPopUpComponent {

  
  loginWidth:string = '65rem';
  loginHeight:string = '35rem';
  RegisterWidth:string = '60rem';
  RegisterHeight:string = '45rem';

  passwordFormatControl = new FormControl('', [
    Validators.required,
    Validators.minLength(8),
    Validators.maxLength(64),
    Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).*$/)
  ]);

  usernameFormatControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3),
    Validators.maxLength(255),
    Validators.pattern(/^[A-Za-z0-9_-]+$/)
  ]);

  constructor(
  
    public dialogRef: MatDialogRef<ConnectionPopUpComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private backendService: BackendService, 
    private usernameCheckerService: UsernameAvailabilityCheckerService
  ){
    if(this.data.loginMode){
      this.dialogRef.updateSize(this.loginWidth, this.loginHeight);
    }else{
      this.dialogRef.updateSize(this.RegisterWidth, this.RegisterHeight);
    }
    
  }

  public isModeLogin: boolean = true;
  public usernameIsAvailable = true;

  onNoClick(): void {
    this.dialogRef.close();
  }

  toggleModeLogin(): void{
    this.isModeLogin = (this.isModeLogin === true) ? false : true;
    if(this.isModeLogin){
      this.dialogRef.updateSize(this.loginWidth, this.loginHeight);
    }else{
      this.dialogRef.updateSize(this.RegisterWidth, this.RegisterHeight);
    }
  }

  registerUsername:string = '';
  registerPassword:string = '';

  registerNewAndApprovedUser(){
    const username = this.registerUsername;
    const password = this.registerPassword;
    //this.backendService.sendRegisterRequest(username, password);
    return this.backendService.sendRegisterRequestFromAuth(username, password);
  }

  loginUsername:string = '';
  loginPassword:string = '';

  sendLoginForm(){
    const username = this.loginUsername;
    const password = this.loginPassword;
    return this.backendService.sendLoginRequestFromAuth(username, password);
  }
  //TODO pipe it up : 1 start animation 2 trylogin 3 stop animation close popup


  isLoading = false;
  
  setLoading(state: boolean) {
    this.isLoading = state;
  }   


  login() {
    of(null).pipe(
          tap(() => this.setLoading(true)),
          concatMap(() => this.sendLoginForm()),
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


  register() {
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

  //Only for developpement selection
  superLogMe(){
    this.backendService.superLogMe();
  }

  checkUserNameAvailability(){
    const name: string = this.registerUsername;
    if(name === ""){
      return;
    }
    this.usernameCheckerService.checkUserNameAvailability(name).subscribe({
      next: (result) => {
        this.usernameIsAvailable = result as boolean;
      }
    });
  }
}
