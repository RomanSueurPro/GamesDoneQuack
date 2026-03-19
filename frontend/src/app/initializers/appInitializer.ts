import { inject } from '@angular/core';
import { CsrfService } from '../services/csrf.service'; 
import { firstValueFrom, forkJoin, catchError, of, tap } from 'rxjs';

import { AuthService } from '../services/auth.service';
import { AdminRoleNameService} from '../services/admin-role-name.service';


export function appInitializer() {
  const authService = inject(AuthService);
  const csrfService = inject(CsrfService);
  const adminRoleNameService = inject(AdminRoleNameService);

  return () => firstValueFrom(
    forkJoin({
      auth: authService.checkLoginObservable().pipe(
        catchError(() => of(false))
      ),
      csrf: csrfService.loadUpObservable().pipe(
        catchError(() => of(false))
      ),
      adminRoleName: adminRoleNameService.fetchAdminNameObservable().pipe(
        catchError(() => of(false))
      )
    })
  );
}