import { Route } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';

export const appRoutes: Route[] = [
    { path: 'login', component: LoginComponent, data: { hideHeader: true }},
    // { path: 'rooms' },
    // { path: 'createroom' },
    // { path: 'play' },
    { path: '' , component: HomeComponent, data: { hideHeader: false }},
  
    //{ path: '**' },
  ];
