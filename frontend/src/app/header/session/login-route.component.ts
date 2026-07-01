import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ConnectionPopUpComponent } from '../connection-pop-up/connection-pop-up.component';
import { AuthService } from '../../services/auth.service';
import { AuthStateService } from '../../services/auth-state.service';
import { ActivatedRoute } from '@angular/router';

@Component({ template: '' })
export class LoginRouteComponent implements OnInit {
  constructor(private dialog: MatDialog, private router: Router,
    private authService: AuthService, private authState: AuthStateService,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    // open the popup immediately
    
    this.authService.checkLoginObservable().subscribe({
      next: () => {
        if(this.authState.isLoggedIn()){
          const redirect =
    this.route.snapshot.queryParamMap.get('redirect') || '/profile';
    this.router.navigateByUrl(redirect);
    
        }else{
          const dialogRef = this.dialog.open(ConnectionPopUpComponent, {
          data: {
            loginMode: true
          },
          disableClose: false,
          hasBackdrop: true,
          panelClass: 'dialog',
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