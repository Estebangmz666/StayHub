import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {ReservationRequestDTO, ReservationResponseDTO, ReservationUpdateDTO} from '../models/reservation';
import {environmentProd} from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {
  private apiUrl = `${environmentProd.apiUrl}${environmentProd.apiVersion}/reservations`;

  constructor(private http: HttpClient) {}

  // Crear reserva (GUEST)
  createReservation(data: ReservationRequestDTO, userEmail: string): Observable<ReservationResponseDTO> {
    const headers = new HttpHeaders({ 'X-User-Email': userEmail });

    return this.http.post<ReservationResponseDTO>(this.apiUrl, data, { headers })
      .pipe(
        tap(response => {
          console.log('Reserva creada:', response);
        }),
        catchError(this.handleError)
      );
  }

  // Obtener una reserva por ID
  getReservation(id: number, userEmail: string): Observable<ReservationResponseDTO> {
    const headers = new HttpHeaders({ 'X-User-Email': userEmail });

    return this.http.get<ReservationResponseDTO>(`${this.apiUrl}/${id}`, { headers })
      .pipe(
        tap(response => {
          console.log('Reserva obtenida:', response);
        }),
        catchError(this.handleError)
      );
  }

  updateReservation(id: number, data: ReservationUpdateDTO, userEmail: string): Observable<ReservationResponseDTO> {
    const headers = new HttpHeaders({ 'X-User-Email': userEmail });

    return this.http.put<ReservationResponseDTO>(`${this.apiUrl}/${id}`, data, { headers })
      .pipe(
        tap(response => {
          console.log('Reserva actualizada:', response);
        }),
        catchError(this.handleError)
      );
  }

  cancelReservation(id: number, userEmail: string): Observable<{ message: string }> {
    const headers = new HttpHeaders({ 'X-User-Email': userEmail });

    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`, { headers })
      .pipe(
        tap(response => {
          console.log('Reserva cancelada:', response);
        }),
        catchError(this.handleError)
      );
  }

  getMyReservations(
    userEmail: string,
    status?: string,
    page: number = 0,
    size: number = 10
  ): Observable<SearchResponseDTO<ReservationResponseDTO>> {
    const headers = new HttpHeaders({ 'X-User-Email': userEmail });

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) {
      params = params.set('status', status);
    }

    return this.http.get<SearchResponseDTO<ReservationResponseDTO>>(this.apiUrl, { headers, params })
      .pipe(
        tap(response => {
          console.log('Mis reservas obtenidas:', response);
        }),
        catchError(this.handleError)
      );
  }

  // Listar reservas de una propiedad (HOST)
  getAccommodationReservations(
    accommodationId: number,
    userEmail: string,
    status?: string,
    page: number = 0,
    size: number = 10
  ): Observable<SearchResponseDTO<ReservationResponseDTO>> {
    const headers = new HttpHeaders({ 'X-User-Email': userEmail });

    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (status) {
      params = params.set('status', status);
    }

    const url = `${this.apiUrl}/accommodations/${accommodationId}`;

    return this.http.get<SearchResponseDTO<ReservationResponseDTO>>(url, { headers, params })
      .pipe(
        tap(response => {
          console.log('Reservas de propiedad obtenidas:', response);
        }),
        catchError(this.handleError)
      );
  }

  // Maneja errores HTTP
  private handleError(error: HttpErrorResponse): Observable<never> {
  let errorMessage = 'Ocurrió un error inesperado';

  if (error.error instanceof ErrorEvent) {
    errorMessage = `Error de red: ${error.error.message}`;
  } else {
    switch (error.status) {
      case 204:
        errorMessage = 'No se encontraron reservas';
        break;
      case 400:
        errorMessage =
          error.error?.message ||
          'Datos inválidos. Verifica las fechas y número de huéspedes';
        break;
      case 403:
        errorMessage = 'No tienes permiso para realizar esta acción';
        break;
      case 404:
        errorMessage = 'Reserva no encontrada';
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
  return throwError(() => new Error(errorMessage)); // <- aquí está la diferencia
}
}

interface SearchResponseDTO<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
}
