import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { JwtUtils } from '../utils/JwtUtils';

// Interface para el usuario (basada en tu UserResponseDTO)
export interface User {
  id: number;
  email: string;
  name: string;
  phoneNumber: string;
  birthDate: string;
  role: 'GUEST' | 'HOST';
  profilePicture?: string;
  description?: string;
  legalDocuments?: string[];
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/v1';

  constructor(private http: HttpClient) {}

  // Obtiene el usuario actual (el que está logueado)
  getCurrentUser(): Observable<User> {
    // Obtiene el ID del token JWT
    const userId = JwtUtils.getUserIdFromToken();

    if (!userId) {
      return throwError(() => new Error('No se pudo obtener el ID del usuario del token'));
    }

    return this.getUserById(userId);
  }

  // Obtiene un usuario por ID
  getUserById(userId: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/users/${userId}`)
      .pipe(
        tap(user => {
          console.log('Usuario obtenido:', user);
        }),
        catchError(this.handleError)
      );
  }

  updateUser(userId: string, userData: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/${userId}`, userData)
      .pipe(
        tap(user => {
          console.log('Usuario actualizado:', user);
        }),
        catchError(this.handleError)
      );
  }

  // Maneja errores
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Error al obtener datos del usuario';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error de red: ${error.error.message}`;
    } else {
      switch (error.status) {
        case 401:
          errorMessage = 'No autorizado. Inicia sesión nuevamente';
          break;
        case 403:
          errorMessage = 'No tienes permiso para ver esta información';
          break;
        case 404:
          errorMessage = 'Usuario no encontrado';
          break;
        case 500:
          errorMessage = 'Error del servidor';
          break;
        case 0:
          errorMessage = 'No se pudo conectar con el servidor';
          break;
        default:
          errorMessage = error.error?.message || `Error ${error.status}`;
      }
    }

    console.error('Error:', error);
    return throwError(() => new Error(errorMessage));
  }
}
