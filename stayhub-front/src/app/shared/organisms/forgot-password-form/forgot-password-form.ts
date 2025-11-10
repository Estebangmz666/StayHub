import { Component, Output, EventEmitter } from '@angular/core';
import { ForgotPasswordErrors } from '../../../models/password-reset';
import { Password } from '../../../validators/Password';
import {LinkComponent} from '../../atoms/link/link';
import {Button} from '../../atoms/button/button';
import {FormField} from '../../molecules/form-field/form-field';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-forgot-password-form',
  standalone: true,
  templateUrl: './forgot-password-form.html',
  imports: [
    LinkComponent,
    Button,
    FormField,
    FormsModule
  ],
  styleUrls: ['./forgot-password-form.css']
})
export class ForgotPasswordFormComponent {
  email: string = '';
  errors: ForgotPasswordErrors = {};
  isLoading: boolean = false;

  @Output() requestReset = new EventEmitter<string>();
  @Output() goToLogin = new EventEmitter<void>();

  validateForm(): boolean {
    this.errors = {};

    if (!this.email) {
      this.errors.email = 'El correo electrónico es obligatorio';
      return false;
    }

    if (!Password.isValidEmail(this.email)) {
      this.errors.email = 'Ingresa un correo válido';
      return false;
    }

    return true;
  }

  handleSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    this.requestReset.emit(this.email);

    setTimeout(() => {
      this.isLoading = false;
    }, 5000);
  }

  onGoToLogin(): void {
    this.goToLogin.emit();
  }
}
