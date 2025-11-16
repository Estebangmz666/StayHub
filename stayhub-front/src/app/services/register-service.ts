import { Injectable } from '@angular/core';
import {environmentDev} from '../../environments/environment.dev';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {UserRegistrationDTO, UserResponseDTO} from '../models/register';
import {catchError, Observable, tap, throwError} from 'rxjs';
import {environmentProd} from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  private readonly baseUrl = `${environmentProd.apiUrl}${environmentProd.apiVersion}/users`;

  constructor(private http: HttpClient) {}

  register(userData: UserRegistrationDTO): Observable<UserResponseDTO> {
    return this.http.post<UserResponseDTO>(`${this.baseUrl}/register`, userData)
      .pipe(
        tap(response => {
          console.log('Registro exitoso:', response);
        }),
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    const errorMap: Record<number, string> = {
      0: "No se puedo conectar con el servidor",
      400: "Datos invalidos, por favor, verifique los campos",
      409: "El correo ya esta registrado",
      500: "Error del servidor, intente mas tarde",
    };

    const errorMessage = error.error instanceof ErrorEvent
      ? `Error de red: ${error.error.message}`
      : errorMap[error.status] || error.error?.message || `Error ${error.status}: ${error.statusText}`;

    console.error("Error en la petición: ", error);
    return throwError(() => new Error(errorMap[error.status] || errorMessage));
  }
}
