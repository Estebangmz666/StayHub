import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegisterService } from '../../../services/register-service';
import { RegisterFormData, UserRegistrationDTO, Role } from '../../../models/register';
import {RegisterFormComponent} from '../../organisms/register-form/register-form';

@Component({
  selector: 'app-register-page',
  standalone: true,
  templateUrl: './register.html',
  imports: [
    RegisterFormComponent
  ],
  styleUrls: ['./register.css']
})
export class RegisterPageComponent {
  isLoading = false;
  errorMessage = '';

  constructor(
    private registerService: RegisterService,
    private router: Router
  ) {}

  handleRegister(formData: RegisterFormData): void {
    this.isLoading = true;
    this.errorMessage = '';

    const userData: UserRegistrationDTO = {
      email: formData.email,
      password: formData.password,
      name: formData.name,
      phoneNumber: formData.phoneNumber,
      birthDate: formData.birthDate,
      role: formData.role
    };

    if (formData.role === Role.HOST) {
      if (formData.profilePicture?.trim()) {
        userData.profilePicture = formData.profilePicture.trim();
      }

      if (formData.description?.trim()) {
        userData.description = formData.description.trim();
      }

      const legalDocs = [
        formData.legalDocument1,
        formData.legalDocument2,
        formData.legalDocument3
      ]
        .filter(doc => doc && doc.trim() !== '')
        .map(doc => doc!.trim());

      if (legalDocs.length > 0) {
        userData.legalDocuments = legalDocs;
      }
    }

    console.log('Enviando datos de registro:', userData);

    this.registerService.register(userData).subscribe({
      next: (response) => {
        console.log('Registro exitoso:', response);
        this.isLoading = false;

        alert(`¡Bienvenido a Stayhub, ${response.name}! 🎉\n\nTu cuenta ha sido creada exitosamente. Ahora puedes iniciar sesión.`);

        this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Error en registro:', error);
        this.isLoading = false;
        this.errorMessage = error.message;
        alert(`Error al registrarse: ${error.message}`);
      }
    });
  }

  handleGoToLogin(): void {
    this.router.navigate(['/login']);
  }
}
