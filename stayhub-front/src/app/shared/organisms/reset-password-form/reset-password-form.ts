import { Component, Output, EventEmitter } from '@angular/core';
import { ResetPasswordErrors } from '../../../models/password-reset';
import { Password } from '../../../validators/Password';
import { PASSWORD_RESET_CONSTANTS } from '../../../constants/password-reset';
import {LinkComponent} from '../../atoms/link/link';
import {Button} from '../../atoms/button/button';
import {PasswordFieldComponent} from '../../molecules/password-field/password-field';
import {FormField} from '../../molecules/form-field/form-field';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-reset-password-form',
  standalone: true,
  templateUrl: './reset-password-form.html',
  imports: [
    LinkComponent,
    Button,
    PasswordFieldComponent,
    FormField,
    FormsModule
  ],
  styleUrls: ['./reset-password-form.css']
})
export class ResetPasswordFormComponent {
  constants = PASSWORD_RESET_CONSTANTS;

  token: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  errors: ResetPasswordErrors = {};
  isLoading: boolean = false;

  @Output() resetPassword = new EventEmitter<{ token: string; newPassword: string }>();
  @Output() goToLogin = new EventEmitter<void>();

  validateForm(): boolean {
    this.errors = {};

    if (!this.token) {
      this.errors.token = 'El código es obligatorio';
    } else if (!Password.isValidToken(this.token)) {
      this.errors.token = 'El código debe tener al menos 20 caracteres';
    }

    if (!this.newPassword) {
      this.errors.newPassword = 'La nueva contraseña es requerida';
    } else if (!Password.isValidPassword(this.newPassword)) {
      this.errors.newPassword = this.constants.PASSWORD_REQUIREMENTS;
    }

    if (!this.confirmPassword) {
      this.errors.confirmPassword = 'Confirma tu nueva contraseña';
    } else if (this.newPassword !== this.confirmPassword) {
      this.errors.confirmPassword = 'Las contraseñas no coinciden';
    }

    return Object.keys(this.errors).length === 0;
  }

  handleSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    this.resetPassword.emit({
      token: this.token,
      newPassword: this.newPassword
    });

    setTimeout(() => {
      this.isLoading = false;
    }, 5000);
  }

  onGoToLogin(): void {
    this.goToLogin.emit();
  }
}
