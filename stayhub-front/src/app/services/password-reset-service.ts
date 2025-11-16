import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {
  PasswordResetRequestDTO,
  ResetPasswordDTO,
  SuccessResponseDTO
} from '../models/password-reset';
import {environmentDev} from '../../environments/environment.dev';
import {environmentProd} from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class PasswordResetService {
  private readonly baseUrl = `${environmentProd.apiUrl}${environmentProd.apiVersion}/users`;

  constructor(private http: HttpClient) {}

  // Paso 1: Solicitar reset (envía email con token)
  requestPasswordReset(email: string): Observable<void> {
    const requestData: PasswordResetRequestDTO = { email };

    return this.http.post<void>(`${this.baseUrl}/request-password-reset`, requestData)
      .pipe(
        tap(() => {
          console.log('Solicitud de reset enviada para:', email);
        }),
        catchError(this.handleError)
      );
  }

  resetPassword(token: string, newPassword: string): Observable<SuccessResponseDTO> {
    const resetData: ResetPasswordDTO = { token, newPassword };

    return this.http.post<SuccessResponseDTO>(`${this.baseUrl}/reset-password`, resetData)
      .pipe(
        tap(response => {
          console.log('Contraseña reseteada:', response.message);
        }),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    const errorMap: Record<number, string> = {
      0: "No se pudo conectar con el servidor",
      400: "Datos Invalidos",
      401: "Token invalido o expirado",
      404: "Usuario no encontrado",
      429: "Demasiadas solicitudes. Intenta mas tarde",
      500: "Error del servidor. Intenta mas tarde",
    }

    const errorMessage = error.error instanceof  ErrorEvent
      ? `Error de red: ${error.error.message}`
      : errorMap[error.status] || error.error?.message || `Error ${error.status}: ${error.statusText}`;

    console.error("Error en la peticion: ", error);
    return throwError(() => new Error(errorMap[error.status] || errorMessage));
  }
}
