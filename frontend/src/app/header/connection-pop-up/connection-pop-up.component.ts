import { Component, Inject, ViewChild, ElementRef } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialogRef,
  } from '@angular/material/dialog';
import { BackendService } from '../../services/backend.service';
import { CommonModule } from '@angular/common';
import { FormControl, FormsModule, Validators, ReactiveFormsModule,AbstractControl, FormGroup,
   ValidationErrors, ValidatorFn } from "@angular/forms";
import { concatMap } from 'rxjs/operators';
import { of, tap } from 'rxjs';
import { LoadingDotsComponent } from '../../animations/loading-dots/loading-dots.component';
import { modeSwitchAnimation } from './connection-pop-up-animation';
import { UsernameAvailabilityCheckerService } from '../../services/username-availability-checker.service';
import { SnackbarService } from '../../services/snackbar.service';
import { EmailAvailabilityCheckerServiceService } from '../../services/email-availability-checker-service.service';


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

  formLogin  = new FormGroup({
      identifier: new FormControl<string>(''),
      password: new FormControl<string>(''),
    });

  usernameFormatControl = new FormControl('', [
    Validators.required,
    Validators.minLength(3),
    Validators.maxLength(255),
    Validators.pattern(/^[A-Za-z0-9_-]+$/)
  ]);

  passwordFormatControl = new FormControl('', [
    Validators.required,
    Validators.minLength(8),
    Validators.maxLength(64),
    Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).*$/)
  ]);

  emailFormatControl = new FormControl('', [
    Validators.required,
    Validators.maxLength(64),
    Validators.pattern(/^((?!\.)[\w\-_.]*[^.])(@\w+)(\.\w+(\.\w+)?[^.\W])$/)
  ]);

  formRegister  = new FormGroup({
      username: this.usernameFormatControl,
      password: this.passwordFormatControl,
      email: this.emailFormatControl,
      
    });  
  
  loginWidth:string = '65rem';
  loginHeight:string = '35rem';
  RegisterWidth:string = '60rem';
  RegisterHeight:string = '65rem';

  constructor(
  
    public dialogRef: MatDialogRef<ConnectionPopUpComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private backendService: BackendService, 
    private usernameCheckerService: UsernameAvailabilityCheckerService,
    private emailCheckerService: EmailAvailabilityCheckerServiceService,
    private snackBarService: SnackbarService
  ){
    if(this.data.loginMode){
      this.dialogRef.updateSize(this.loginWidth, this.loginHeight);
    }else{
      this.dialogRef.updateSize(this.RegisterWidth, this.RegisterHeight);
    }
    
  }

  public isModeLogin: boolean = true;
  public usernameIsAvailable = true;
  public emailIsAvailable = true;

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


  registerNewAndApprovedUser(){
    console.log(this.formRegister.value);
    return this.backendService.sendRegisterRequestFromAuth(this.formRegister);
  }

  sendLoginForm(){
    return this.backendService.sendLoginRequestFromAuth(this.formLogin);
  }


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
        this.backendService.fetchCsrf();
        this.onNoClick();
      },
      error: (error) => {
        console.log('Login failed', error);
        this.setLoading(false);
        this.snackBarService.showErrorSnackBar(error);
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
        this.backendService.fetchCsrf();
        this.onNoClick();
      },
      error: (error) => {
        console.log('Register error', error);
        this.setLoading(false);
        this.snackBarService.showErrorSnackBar(error);
      }
    });
  }

  //Only for developpement selection
  superLogMe(){
    this.backendService.superLogMe();
  }

  checkUserNameAvailability(){
    const name: string | null | undefined = this.formRegister.value.username;
    if(name === "" || name === undefined || name === null){
      return;
    }
    this.usernameCheckerService.checkUserNameAvailability(name).subscribe({
      next: (result) => {
        this.usernameIsAvailable = result as boolean;
      }
    });
  }
  
  public passwordClass: String = "show-pass-icon";
  public visiblePass: boolean = false;

  togglePasswordVisibility(){
    this.passwordClass = (this.passwordClass === "show-pass-icon" ? "hide-pass-icon" : "show-pass-icon");
    this.visiblePass = !this.visiblePass;
  }


  checkEmailAvailability(){
    const email: string | null | undefined = this.formRegister.value.email;
    if(email === "" || email === undefined || email === null){
      return;
    }
    this.emailCheckerService.checkEmailAvailability(email).subscribe({
      next: (result) => {
        this.emailIsAvailable = result as boolean;
      }
    });
  }
}
