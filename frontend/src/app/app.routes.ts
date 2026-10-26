import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginRouteComponent } from './header/session/login-route.component';
import { AdminComponent } from './pages/admin/admin.component';
import { adminGuard } from './guards/admin.guard';
import { unsavedChangesGuard } from './guards/unsaved-changes.guard';
import { BannedPageComponent } from './pages/banned-page/banned-page.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [

    {
        path: '',
        pathMatch: 'full',
        component: HomeComponent,
        title: 'home'
    },
    {
        path: "",
        canActivateChild: [authGuard],
        children: [
            {
                path: "profile",
                loadComponent: () =>
                    import('./pages/profile/profile.component')
                        .then(m => m.ProfileComponent)
            },
        ]
    },

    { path: 'login', component: LoginRouteComponent },

    {
        path: 'admin',
        title: "admin",
        component: AdminComponent,
        canActivate: [adminGuard],
        canDeactivate: [unsavedChangesGuard]
    },
    { path: 'banned', component: BannedPageComponent },
];
