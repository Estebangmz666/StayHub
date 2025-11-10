import { Component, Output, EventEmitter } from '@angular/core';
import {FormField} from '../../molecules/form-field/form-field';
import {FormsModule} from '@angular/forms';
import {PasswordFieldComponent} from '../../molecules/password-field/password-field';
import {LinkComponent} from '../../atoms/link/link';
import {Button} from '../../atoms/button/button';

export interface LoginCredentials {
  email: string;
  password: string;
}

interface ValidationErrors {
  email?: string;
  password?: string;
}

@Component({
  selector: 'app-login-form',
  standalone: true,
  templateUrl: './login-form.html',
  imports: [
    FormField,
    FormsModule,
    PasswordFieldComponent,
    LinkComponent,
    Button
  ],
  styleUrls: ['./login-form.css']
})
export class LoginFormComponent {
  credentials: LoginCredentials = {
    email: '',
    password: ''
  };

  errors: ValidationErrors = {};
  isLoading: boolean = false;

  @Output() loginSubmit = new EventEmitter<LoginCredentials>();
  @Output() forgotPassword = new EventEmitter<void>();
  @Output() signUp = new EventEmitter<void>();

  validateForm(): boolean {
    this.errors = {};

    if (!this.credentials.email) {
      this.errors.email = 'El correo electrónico es obligatorio';
    } else if (!this.isValidEmail(this.credentials.email)) {
      this.errors.email = 'Ingresa un correo electrónico válido';
    }

    if (!this.credentials.password) {
      this.errors.password = 'La contraseña es requerida';
    } else if (this.credentials.password.length < 6) {
      this.errors.password = 'La contraseña debe tener al menos 6 caracteres';
    }

    return Object.keys(this.errors).length === 0;
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  handleLogin(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;

    this.loginSubmit.emit(this.credentials);

    setTimeout(() => {
      this.isLoading = false;
    }, 5000);
  }

  onForgotPassword(): void {
    this.forgotPassword.emit();
  }

  onSignUp(): void {
    this.signUp.emit();
  }
}
