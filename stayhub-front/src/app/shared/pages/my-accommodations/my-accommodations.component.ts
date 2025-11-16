import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AccommodationService } from '../../../services/accommodation-service';
import { AuthService } from '../../../services/auth-service';
import { AccommodationResponseDTO } from '../../../models/accommodation';
import { AccommodationUtils } from '../../../utils/accommodation';

@Component({
  selector: 'app-my-accommodations',
  standalone: true,
  templateUrl: './my-accommodations.component.html',
  styleUrls: ['./my-accommodations.component.css']
})
export class MyAccommodationsComponent implements OnInit {
  accommodations: AccommodationResponseDTO[] = [];
  isLoading = true;
  error: string = '';

  // Paginación
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  // Para el modal de eliminar
  showDeleteModal = false;
  accommodationToDelete: AccommodationResponseDTO | null = null;
  isDeleting = false;

  // Utilidades
  utils = AccommodationUtils;

  // Username del usuario logueado
  username: string = '';

  constructor(
    private accommodationService: AccommodationService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Obtiene el username del usuario logueado
    const userInfo = this.authService.getCurrentUserInfo();
    if (userInfo?.email) {
      this.username = userInfo.email;
      this.loadMyAccommodations();
    } else {
      this.error = 'No se pudo obtener la información del usuario';
      this.isLoading = false;
    }
  }

  loadMyAccommodations(): void {
    this.isLoading = true;
    this.error = '';

    this.accommodationService.getMyAccommodations(this.username, this.currentPage, this.pageSize)
      .subscribe({
        next: (response) => {
          this.accommodations = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = Math.ceil(this.totalElements / this.pageSize);
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error cargando mis propiedades:', error);
          this.error = error.message;
          this.isLoading = false;

          if (error.message.includes('No se encontraron')) {
            this.accommodations = [];
          }
        }
      });
  }

  // Navega a crear nueva propiedad
  createAccommodation(): void {
    this.router.navigate(['/accommodations/new']);
  }

  // Navega a ver detalle
  viewAccommodation(id: number): void {
    this.router.navigate(['/accommodations', id]);
  }

  // Navega a editar
  editAccommodation(id: number): void {
    this.router.navigate(['/accommodations', id, 'edit']);
  }

  // Abre modal de confirmación para eliminar
  confirmDelete(accommodation: AccommodationResponseDTO): void {
    this.accommodationToDelete = accommodation;
    this.showDeleteModal = true;
  }

  // Cancela el modal de eliminar
  cancelDelete(): void {
    this.showDeleteModal = false;
    this.accommodationToDelete = null;
  }

  // Elimina la propiedad
  deleteAccommodation(): void {
    if (!this.accommodationToDelete) return;

    this.isDeleting = true;
    const id = this.accommodationToDelete.id;

    this.accommodationService.deleteAccommodation(id, this.username)
      .subscribe({
        next: () => {
          console.log('Propiedad eliminada');
          this.isDeleting = false;
          this.showDeleteModal = false;
          this.accommodationToDelete = null;

          // Recarga la lista
          this.loadMyAccommodations();

          alert('Propiedad eliminada exitosamente');
        },
        error: (error) => {
          console.error('Error eliminando propiedad:', error);
          this.isDeleting = false;
          alert(`Error al eliminar: ${error.message}`);
        }
      });
  }

  // Paginación
  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadMyAccommodations();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadMyAccommodations();
    }
  }

  goToPage(page: number): void {
    this.currentPage = page;
    this.loadMyAccommodations();
  }

  // Helpers
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

  getCapacityText(capacity: number): string {
    return capacity === 1 ? '1 huésped' : `${capacity} huéspedes`;
  }
}
