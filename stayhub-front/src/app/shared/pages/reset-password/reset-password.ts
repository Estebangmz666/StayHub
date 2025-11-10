import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { PasswordResetService } from '../../../services/password-reset-service';
import {ResetPasswordFormComponent} from '../../organisms/reset-password-form/reset-password-form';

@Component({
  selector: 'app-reset-password-page',
  standalone: true,
  templateUrl: './reset-password.html',
  imports: [
    ResetPasswordFormComponent
  ],
  styleUrls: ['./reset-password.css']
})
export class ResetPasswordPageComponent {
  constructor(
    private passwordResetService: PasswordResetService,
    private router: Router
  ) {}

  handleResetPassword(data: { token: string; newPassword: string }): void {
    this.passwordResetService.resetPassword(data.token, data.newPassword)
      .subscribe({
        next: (response) => {
          console.log('Contraseña reseteada:', response);
          alert(`✅ ${response.message}\n\nAhora puedes iniciar sesión con tu nueva contraseña.`);
          this.router.navigate(['/login']);
        },
        error: (error) => {
          console.error('Error:', error);
          alert(`Error: ${error.message}`);
        }
      });
  }

  handleGoToLogin(): void {
    this.router.navigate(['/login']);
  }
}
