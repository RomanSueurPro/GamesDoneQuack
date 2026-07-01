import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthStateService } from '../services/auth-state.service';


export const adminGuard: CanActivateFn = (route, state) => {

  const authStateService = inject(AuthStateService);
  const router = inject(Router);

  if(authStateService.isLoggedIn()
    && authStateService.isAdmin()
    ){
    return true;
  }

  if(authStateService.isLoggedIn()){
    console.log(state);
    return router.createUrlTree(
      ['/'],
    );
  }
  return router.createUrlTree(
    ['/login'],
  );
};
