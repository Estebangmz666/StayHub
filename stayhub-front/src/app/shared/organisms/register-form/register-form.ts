import { Component, Output, EventEmitter } from '@angular/core';
import { RegisterFormData, Role, RegisterValidationErrors } from '../../../models/register';
import { RegisterValidators } from "../../../validators/Register"
import { REGISTER_CONSTANTS } from '../../../constants/register';
import {FormsModule} from '@angular/forms';
import {FormField} from '../../molecules/form-field/form-field';
import {PasswordFieldComponent} from '../../molecules/password-field/password-field';
import {Label} from '../../atoms/label/label';
import {ErrorText} from '../../atoms/error-text/error-text';
import {Button} from '../../atoms/button/button';
import {LinkComponent} from '../../atoms/link/link';

@Component({
  selector: 'app-register-form',
  standalone: true,
  templateUrl: './register-form.html',
  imports: [
    FormsModule,
    FormField,
    PasswordFieldComponent,
    Label,
    ErrorText,
    Button,
    LinkComponent
  ],
  styleUrls: ['./register-form.css']
})
export class RegisterFormComponent {
  Role = Role;
  constants = REGISTER_CONSTANTS;

  formData: RegisterFormData = {
    email: '',
    password: '',
    confirmPassword: '',
    name: '',
    phoneNumber: '+57 ',
    birthDate: '',
    role: Role.GUEST,
    profilePicture: '',
    description: '',
    legalDocument1: '',
    legalDocument2: '',
    legalDocument3: ''
  };

  errors: RegisterValidationErrors = {};

  isLoading: boolean = false;

  @Output() registerSubmit = new EventEmitter<RegisterFormData>();
  @Output() goToLogin = new EventEmitter<void>();

  validateForm(): boolean {
    this.errors = {};

    if (!this.formData.email) {
      this.errors.email = 'El correo electrónico es obligatorio';
    } else if (!RegisterValidators.isValidEmail(this.formData.email)) {
      this.errors.email = 'Ingresa un correo válido';
    }

    if (!this.formData.password) {
      this.errors.password = 'La contraseña es obligatorio';
    } else if (!RegisterValidators.isValidPassword(this.formData.password)) {
      this.errors.password = this.constants.PASSWORD_REQUIREMENTS;
    }

    if (!this.formData.confirmPassword) {
      this.errors.confirmPassword = 'Confirma tu contraseña';
    } else if (this.formData.password !== this.formData.confirmPassword) {
      this.errors.confirmPassword = 'Las contraseñas no coinciden';
    }

    if (!this.formData.name) {
      this.errors.name = 'El nombre es obligatorio';
    } else if (this.formData.name.length > 100) {
      this.errors.name = 'El nombre no puede exceder 100 caracteres';
    }

    if (!this.formData.phoneNumber) {
      this.errors.phoneNumber = 'El teléfono es obligatorio';
    } else if (!RegisterValidators.isValidPhoneNumber(this.formData.phoneNumber)) {
      this.errors.phoneNumber = this.constants.PHONE_FORMAT;
    }

    if (!this.formData.birthDate) {
      this.errors.birthDate = 'La fecha de nacimiento es requerida';
    } else {
      const dateValidation = RegisterValidators.isValidBirthDate(this.formData.birthDate);
      if (!dateValidation.valid) {
        this.errors.birthDate = dateValidation.message;
      }
    }

    if (this.formData.role === Role.HOST) {
      if (this.formData.profilePicture && !RegisterValidators.isValidUrl(this.formData.profilePicture)) {
        this.errors.profilePicture = 'Debe ser una URL válida';
      }

      if (this.formData.description && this.formData.description.length > 500) {
        this.errors.description = 'La descripción no puede exceder 500 caracteres';
      }

      if (this.formData.legalDocument1 && !RegisterValidators.isValidUrl(this.formData.legalDocument1)) {
        this.errors.legalDocument1 = 'Debe ser una URL válida';
      }
      if (this.formData.legalDocument2 && !RegisterValidators.isValidUrl(this.formData.legalDocument2)) {
        this.errors.legalDocument2 = 'Debe ser una URL válida';
      }
      if (this.formData.legalDocument3 && !RegisterValidators.isValidUrl(this.formData.legalDocument3)) {
        this.errors.legalDocument3 = 'Debe ser una URL válida';
      }
    }

    return Object.keys(this.errors).length === 0;
  }

  onRoleChange(role: Role): void {
    this.formData.role = role;

    if (role === Role.GUEST) {
      this.formData.profilePicture = '';
      this.formData.description = '';
      this.formData.legalDocument1 = '';
      this.formData.legalDocument2 = '';
      this.formData.legalDocument3 = '';

      delete this.errors.profilePicture;
      delete this.errors.description;
      delete this.errors.legalDocument1;
      delete this.errors.legalDocument2;
      delete this.errors.legalDocument3;
    }
  }

  onPhoneInput(): void {
    this.formData.phoneNumber = RegisterValidators.formatPhoneNumber(this.formData.phoneNumber);
  }

  handleSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    this.registerSubmit.emit(this.formData);

    setTimeout(() => {
      this.isLoading = false;
    }, 5000);
  }

  onGoToLogin(): void {
    this.goToLogin.emit();
  }

  getAge(): number | null {
    if (!this.formData.birthDate) return null;

    const birthDate = new Date(this.formData.birthDate);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    return age;
  }
}
