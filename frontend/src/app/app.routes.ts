import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginRouteComponent } from './header/session/login-route.component';
import { AdminComponent } from './pages/admin/admin.component';
import { adminGuard } from './guards/admin.guard';
import { unsavedChangesGuard } from './guards/unsaved-changes.guard';
import { BannedPageComponent } from './pages/banned-page/banned-page.component';
import { authGuard } from './guards/auth.guard';
import { ProfileComponent } from './pages/profile/profile.component';
import { EditComponent } from './pages/profile/edit/edit.component';

export const routes: Routes = [

    {
        path: '',
        pathMatch: 'full',
        component: HomeComponent,
        title: 'home'
    },
    {
        path: "users",
        // canActivateChild: [authGuard],
        children: [
            {
                path: ":username",
                component: ProfileComponent,
            },

        ]
    },
    {
        path: "editprofile",
        component: EditComponent,
        canActivate: [authGuard],
    },

    {
        path: 'login',
        component: LoginRouteComponent
    },

    {
        path: 'admin',
        title: "admin",
        component: AdminComponent,
        canActivate: [adminGuard],
        canDeactivate: [unsavedChangesGuard]
    },
    {
        path: 'banned',
        component: BannedPageComponent
    },

];
