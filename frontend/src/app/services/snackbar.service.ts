import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class SnackbarService {

  constructor(private snackbar: MatSnackBar) { }

  showErrorSnackBar(error:any):void{
    this.snackbar.open(
      'Code status : ' + error.status + ', ' + error.error.error,
      'Close',
      {
        panelClass: ['failure-snackbar'],
      }
    );
  }

  showSuccessResponseSnackBar(response:any):void{
    this.snackbar.open(
      response.message,
      'Close',
      {
        duration: 3000,
        panelClass: ['success-snackbar'],
      }
    );
  }

  showSuccessMessageSnackBar(successMessage:string):void{
    this.snackbar.open(
      successMessage,
      'Close',
      {
        duration: 3000,
        panelClass: ['success-snackbar'],
      }
    );
  }

  showTestSnackBar(){
    this.snackbar.open(
      "coucou",
    )
    
  }
}
