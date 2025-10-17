import { Routes } from '@angular/router';
import { ProfileComponent } from './profile-component/profile-component.component';
import { HomeComponent } from './home/home.component';
import { LoginRouteComponent } from './header/session/login-route.component';

export const routes: Routes = [
    {
        path: "", title: "home", component: HomeComponent
    },
    {
        path: "profile", title: "profile", component: ProfileComponent
    },
    { path: 'login', component: LoginRouteComponent },
    
];
