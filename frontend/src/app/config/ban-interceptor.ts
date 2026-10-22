import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { Router } from '@angular/router';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

export const Ban_Interceptor: HttpInterceptorFn = (req, next) => {

  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

        console.log(error);
      if (
        error.status === 403 &&
        error.error?.error === 'ACCOUNT_BANNED'
      ) {
        router.navigate(['/banned']);
      }

      return throwError(() => error);
    })
  );
};