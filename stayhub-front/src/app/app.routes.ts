import { Routes } from '@angular/router';
import { LoginPageComponent } from './shared/pages/login/login';
import { RegisterPageComponent } from "./shared/pages/register/register";
import { ForgotPasswordPageComponent } from "./shared/pages/forgot-password/forgot-password";
import { ResetPasswordPageComponent } from './shared/pages/reset-password/reset-password';
import {DashboardComponent} from './shared/pages/dashboard/dashboard.component';
import {authGuard} from './guards/auth';
import {AccommodationsListComponent} from './shared/pages/accommodation-list/accommodation-list.component';
import {AccommodationDetailComponent} from './shared/pages/accommodation-detail/accommodation-detail.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full' },
  {
    path: 'login',
    component: LoginPageComponent
  },
  {
    path: 'register',
    component: RegisterPageComponent
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordPageComponent
  },
  {
    path: 'reset-password',
    component: ResetPasswordPageComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'accommodations',
    component: AccommodationsListComponent
  },
  {
    path: 'accommodations/:id',
    component: AccommodationDetailComponent
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
