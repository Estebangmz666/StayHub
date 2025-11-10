import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { PasswordResetService } from "../../../services/password-reset-service";
import {ForgotPasswordFormComponent} from '../../organisms/forgot-password-form/forgot-password-form';

@Component({
  selector: 'app-forgot-password-page',
  standalone: true,
  templateUrl: './forgot-password.html',
  imports: [
    ForgotPasswordFormComponent
  ],
  styleUrls: ['./forgot-password.css']
})
export class ForgotPasswordPageComponent {
  constructor(
    private passwordResetService: PasswordResetService,
    private router: Router
  ) {}

  handleRequestReset(email: string): void {
    this.passwordResetService.requestPasswordReset(email)
      .subscribe({
        next: () => {
          console.log('Solicitud de reset enviada');
          alert('Si el correo existe en nuestro sistema, recibirás un código para restablecer tu contraseña. Revisa tu bandeja de entrada.');
          // Redirige al paso 2 (reset-password)
          this.router.navigate(['/reset-password']);
        },
        error: (error) => {
          console.error('Error:', error);

          // Por seguridad, mostramos mensaje genérico aunque el email no exista
          if (error.message.includes('no encontrado')) {
            alert('Si el correo existe en nuestro sistema, recibirás un código para restablecer tu contraseña.');
            this.router.navigate(['/reset-password']);
          } else {
            alert(`Error: ${error.message}`);
          }
        }
      });
  }

  handleGoToLogin(): void {
    this.router.navigate(['/login']);
  }
}
