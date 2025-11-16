import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import {
  AccommodationRequestDTO,
  AccommodationUpdateDTO,
  AccommodationResponseDTO,
  SearchResponseDTO,
  AccommodationFilters
} from '../models/accommodation';
import {environmentProd} from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class AccommodationService {
  private readonly apiUrl = `${environmentProd.apiUrl}${environmentProd.apiVersion}/accommodations`;

  constructor(private http: HttpClient) {}

  getMyAccommodations(username: string, page: number = 0, size: number = 10): Observable<SearchResponseDTO<AccommodationResponseDTO>> {
    const headers = new HttpHeaders({ 'X-Username': username });
    const url = `${this.apiUrl}/my-accommodations?page=${page}&size=${size}`;

    return this.http.get<SearchResponseDTO<AccommodationResponseDTO>>(url, { headers })
      .pipe(
        tap(response => {
          console.log('Mis propiedades obtenidas:', response);
        }),
        catchError(this.handleError)
      );
  }

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

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Ocurrió un error inesperado';

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error de red: ${error.error.message}`;
    } else {
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
