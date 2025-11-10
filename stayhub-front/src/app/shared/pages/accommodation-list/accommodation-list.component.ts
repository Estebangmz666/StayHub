import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccommodationService } from '../../../services/accommodation-service';
import { AccommodationResponseDTO, AccommodationFilters } from '../../../models/accommodation';
import { ACCOMMODATION_CONSTANTS } from '../../../constants/accommodation';
import {FormsModule} from '@angular/forms';
import {AccommodationCardComponent} from '../../molecules/accommodation-card/accommodation-card.component';

@Component({
  selector: 'app-accommodations-list',
  standalone: true,
  templateUrl: './accommodation-list.component.html',
  imports: [
    FormsModule,
    AccommodationCardComponent
  ],
  styleUrls: ['./accommodation-list.component.css']
})
export class AccommodationsListComponent implements OnInit {
  accommodations: AccommodationResponseDTO[] = [];
  isLoading = true;
  error: string = '';

  // Paginación
  currentPage = 0;
  pageSize = ACCOMMODATION_CONSTANTS.DEFAULT_PAGE_SIZE;
  totalElements = 0;
  totalPages = 0;

  // Filtros
  filters: AccommodationFilters = {
    city: undefined,
    minCapacity: undefined,
    maxPrice: undefined,
    amenityIds: []
  };

  // Para el selector de ciudad
  cities = ACCOMMODATION_CONSTANTS.CITIES;
  showFilters = false;

  constructor(
    private accommodationService: AccommodationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAccommodations();
  }

  loadAccommodations(): void {
    this.isLoading = true;
    this.error = '';

    this.accommodationService.listAccommodations(this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.accommodations = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = Math.ceil(this.totalElements / this.pageSize);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error cargando propiedades:', error);
          this.error = error.message;
          this.isLoading = false;

          // Si es 204 (no content), no mostrar como error
          if (error.message.includes('No se encontraron')) {
            this.accommodations = [];
          }
        }
      });
  }

  // Aplica los filtros de búsqueda
  applyFilters(): void {
    this.isLoading = true;
    this.error = '';
    this.currentPage = 0;

    const searchFilters: AccommodationFilters = {
      ...this.filters,
      page: this.currentPage,
      size: this.pageSize
    };

    this.accommodationService.searchAccommodations(searchFilters)
      .subscribe({
        next: (response) => {
          this.accommodations = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = Math.ceil(this.totalElements / this.pageSize);
          this.isLoading = false;
          this.showFilters = false;
        },
        error: (error) => {
          console.error('Error en búsqueda:', error);
          this.error = error.message;
          this.isLoading = false;

          if (error.message.includes('No se encontraron')) {
            this.accommodations = [];
          }
        }
      });
  }

  // Limpia los filtros
  clearFilters(): void {
    this.filters = {
      city: undefined,
      minCapacity: undefined,
      maxPrice: undefined,
      amenityIds: []
    };
    this.loadAccommodations();
  }

  // Toggle del panel de filtros
  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  // Navega al detalle de una propiedad
  viewAccommodation(id: number): void {
    this.router.navigate(['/accommodations', id]);
  }

  // Paginación
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadAccommodations();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadAccommodations();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadAccommodations();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // Helpers
  get hasFilters(): boolean {
    return !!(this.filters.city || this.filters.minCapacity || this.filters.maxPrice);
  }

  get paginationArray(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;

    let start = Math.max(0, this.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages, start + maxVisible);

    if (end - start < maxVisible) {
      start = Math.max(0, end - maxVisible);
    }

    for (let i = start; i < end; i++) {
      pages.push(i);
    }

    return pages;
  }
}
