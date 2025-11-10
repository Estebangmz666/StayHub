import { Injectable } from '@angular/core';
import {environmentDev} from '../../environments/environment.dev';
import {BehaviorSubject, catchError, Observable, tap, throwError} from 'rxjs';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {LoginRequest, LoginResponse} from '../models/LoginRequest';
import {JwtUtils} from '../utils/JwtUtils';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly baseUrl = new URL(environmentDev.apiVersion, environmentDev.apiUrl).toString();

  private readonly isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private static readonly AUTH_TOKEN_KEY = 'authToken';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponse> {
    const loginData: LoginRequest = { email, password };

    return this.http.post<LoginResponse>(`${this.baseUrl}/users/login`, loginData)
      .pipe(
        tap(response => {
          if (response.token) {
            localStorage.setItem(AuthService.AUTH_TOKEN_KEY, response.token);
            this.isAuthenticatedSubject.next(true);
            console.log('Token guardado exitosamente');
          }
        }),
        catchError(this.handleError)
      );
  }

  logout(): void {
    localStorage.removeItem(AuthService.AUTH_TOKEN_KEY);
    this.isAuthenticatedSubject.next(false);
    console.log('Sesión cerrada');
  }

  getToken(): string | null {
    return localStorage.getItem(AuthService.AUTH_TOKEN_KEY);
  }

  private hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) return false;

    try {
      const parts = token.split('.');
      if (parts.length !== 3) return false;

      const payload = JSON.parse(atob(parts[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) return false;

    if (JwtUtils.isTokenExpired(token)) {
      this.logout();
      return false
    }
    return true;
  }

  getCurrentUserId(): string | null {
    return JwtUtils.getUserIdFromToken();
  }

  getCurrentUserInfo() {
    return JwtUtils.getUserInfoFromToken();
  }

  private handleError(error: HttpErrorResponse) {
    const errorMap: Record<number, string> = {
      0: 'No se pudo conectar con el servidor. Verifica que esté corriendo',
      401: 'Credenciales inválidas',
      404: 'Usuario no encontrado',
      500: 'Error del servidor. Intenta más tarde',
    };

    const errorMessage = error.error instanceof ErrorEvent
      ? `Error de red: ${error.error.message}`
      : errorMap[error.status] || error.error?.message || `Error ${error.status}: ${error.statusText}`;

    console.error('Error en la petición: ', error);
    return throwError(() => new Error(errorMap[error.status] || errorMessage));
  }

  refreshAuthStatus(): void {
    this.isAuthenticatedSubject.next(this.hasValidToken());
  }
}
