import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {
  AccommodationRequestDTO,
  AccommodationUpdateDTO,
  AccommodationResponseDTO,
  SearchResponseDTO,
  AccommodationFilters
} from '../models/accommodation';

@Injectable({
  providedIn: 'root'
})
export class AccommodationService {
  private apiUrl = 'http://localhost:8080/api/v1/accommodations';

  constructor(private http: HttpClient) {}

  // Lista todas las propiedades (paginado)
  listAccommodations(page: number = 0, size: number = 10): Observable<SearchResponseDTO<AccommodationResponseDTO>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<SearchResponseDTO<AccommodationResponseDTO>>(this.apiUrl, { params })
      .pipe(
        tap(response => {
          console.log('Propiedades obtenidas:', response);
        }),
        catchError(this.handleError)
      );
  }

  // Busca propiedades con filtros
  searchAccommodations(filters: AccommodationFilters): Observable<SearchResponseDTO<AccommodationResponseDTO>> {
    let params = new HttpParams()
      .set('page', (filters.page || 0).toString())
      .set('size', (filters.size || 10).toString());

    if (filters.city) {
      params = params.set('city', filters.city);
    }
    if (filters.minCapacity) {
      params = params.set('minCapacity', filters.minCapacity.toString());
    }
    if (filters.maxPrice) {
      params = params.set('maxPrice', filters.maxPrice.toString());
    }
    if (filters.amenityIds && filters.amenityIds.length > 0) {
      // Para arrays, puedes enviar múltiples parámetros o un array
      filters.amenityIds.forEach(id => {
        params = params.append('amenityIds', id.toString());
      });
    }

    return this.http.get<SearchResponseDTO<AccommodationResponseDTO>>(`${this.apiUrl}/search`, { params })
      .pipe(
        tap(response => {
          console.log('Búsqueda completada:', response);
        }),
        catchError(this.handleError)
      );
  }

  // Obtiene una propiedad por ID
  getAccommodation(id: number): Observable<AccommodationResponseDTO> {
    return this.http.get<AccommodationResponseDTO>(`${this.apiUrl}/${id}`)
      .pipe(
        tap(accommodation => {
          console.log('Propiedad obtenida:', accommodation);
        }),
        catchError(this.handleError)
      );
  }

  // Crea una nueva propiedad (solo HOST)
  createAccommodation(data: AccommodationRequestDTO, username: string): Observable<AccommodationResponseDTO> {
    const headers = { 'X-Username': username };

    return this.http.post<AccommodationResponseDTO>(this.apiUrl, data, { headers })
      .pipe(
        tap(accommodation => {
          console.log('Propiedad creada:', accommodation);
        }),
        catchError(this.handleError)
      );
  }

  // Actualiza una propiedad (solo HOST dueño)
  updateAccommodation(id: number, data: AccommodationUpdateDTO, username: string): Observable<AccommodationResponseDTO> {
    const headers = { 'X-Username': username };

    return this.http.put<AccommodationResponseDTO>(`${this.apiUrl}/${id}`, data, { headers })
      .pipe(
        tap(accommodation => {
          console.log('Propiedad actualizada:', accommodation);
        }),
        catchError(this.handleError)
      );
  }

  // Elimina una propiedad (soft delete, solo HOST dueño)
  deleteAccommodation(id: number, username: string): Observable<{ message: string }> {
    const headers = { 'X-Username': username };

    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`, { headers })
      .pipe(
        tap(response => {
          console.log('Propiedad eliminada:', response);
        }),
        catchError(this.handleError)
      );
  }

  // Maneja errores HTTP
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Ocurrió un error inesperado';

    if (error.error instanceof ErrorEvent) {
      // Error del cliente
      errorMessage = `Error de red: ${error.error.message}`;
    } else {
      // Error del servidor
      switch (error.status) {
        case 204:
          errorMessage = 'No se encontraron propiedades';
          break;
        case 400:
          errorMessage = error.error?.message || 'Datos inválidos';
          break;
        case 403:
          errorMessage = 'No tienes permiso para realizar esta acción';
          break;
        case 404:
          errorMessage = 'Propiedad no encontrada';
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
