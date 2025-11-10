import { Component } from '@angular/core';
import {LoginCredentials, LoginFormComponent} from '../../organisms/login-form/login-form';
import {AuthService} from '../../../services/auth-service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login-page',
  standalone: true,
  templateUrl: './login.html',
  imports: [
    LoginFormComponent
  ],
  styleUrls: ['./login.css']
})
export class LoginPageComponent {
  isLoading = false;
  errorMessage = "";

  constructor(private authService: AuthService,
              private router: Router) {}

  handleLogin(credentials: LoginCredentials): void {
    this.isLoading = true;
    this.errorMessage = "";

    this.authService.login(credentials.email, credentials.password).subscribe(
      {
        next: (response) => {
          console.log("Login Exitoso \n", response);
          this.isLoading = false;
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          console.error("Error en el login: ", error);
          this.isLoading = false;
          this.errorMessage = error.message;
          alert(`Error: ${error.message}`);
        }
      }
    )
  }

  handleForgotPassword(): void {
    console.log('Recuperar contraseña');
    this.router.navigate(['/forgot-password']);
  }

  handleSignUp(): void {
    console.log('Crear cuenta');
    this.router.navigate(['/register']);
  }
}
