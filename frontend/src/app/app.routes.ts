import { Routes } from '@angular/router';
import { ProfileComponentComponent } from './pages/profile-component/profile-component.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginRouteComponent } from './header/session/login-route.component';
import { AdminComponent } from './pages/admin/admin.component';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
    
    { path: "", title: "home", component: HomeComponent},
    { path: "profile", title: "profile", component: ProfileComponentComponent},
    { path: 'login', component: LoginRouteComponent },
    { 
        path: 'admin', 
        title: "admin", 
        component: AdminComponent,
        canActivate: [adminGuard]
    },
];
