import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ConnectionPopUpComponent } from '../connection-pop-up/connection-pop-up.component';
import { AuthService } from '../../services/auth.service';
import { AuthStateService } from '../../services/auth-state.service';

@Component({ template: '' })
export class LoginRouteComponent implements OnInit {
  constructor(private dialog: MatDialog, private router: Router,
    private authService: AuthService, private authState: AuthStateService
  ) {}

  ngOnInit(): void {
    // open the popup immediately
    
    this.authService.checkLoginObservable().subscribe({
      next: () => {
        if(this.authState.isLoggedIn()){
          this.router.navigate(['/profile']);
        }else{
          const dialogRef = this.dialog.open(ConnectionPopUpComponent, {
          height: '200px',
          width: '300px',
          disableClose: false,
          hasBackdrop: true,
          panelClass: 'connection-pop-up',
        });

          // when the popup closes, navigate back to home
          dialogRef.afterClosed().subscribe(() => {
            // use replaceUrl so no extra entry is added to history
            this.router.navigate(['/'], { replaceUrl: true });
          });
        }
      },
      error: (err) => {
        console.log(err);
      }
    })
  }
}