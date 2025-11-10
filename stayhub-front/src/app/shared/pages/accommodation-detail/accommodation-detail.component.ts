import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AccommodationService } from '../../../services/accommodation-service';
import { AccommodationResponseDTO } from '../../../models/accommodation';
import { AccommodationUtils } from '../../../utils/accommodation';

@Component({
  selector: 'app-accommodation-detail',
  standalone: true,
  templateUrl: './accommodation-detail.component.html',
  styleUrls: ['./accommodation-detail.component.css']
})
export class AccommodationDetailComponent implements OnInit {
  accommodation: AccommodationResponseDTO | null = null;
  isLoading = true;
  error: string = '';

  currentImageIndex = 0;
  allImages: string[] = [];
  showGalleryModal = false;

  utils = AccommodationUtils;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accommodationService: AccommodationService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      if (id) {
        this.loadAccommodation(id);
      }
    });
  }

  loadAccommodation(id: number): void {
    this.isLoading = true;
    this.error = '';

    this.accommodationService.getAccommodation(id).subscribe({
      next: (accommodation) => {
        this.accommodation = accommodation;

        this.allImages = [
          accommodation.mainImage,
          ...(accommodation.images || [])
        ];

        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando propiedad:', error);
        this.error = error.message;
        this.isLoading = false;
      }
    });
  }

  previousImage(): void {
    if (this.currentImageIndex > 0) {
      this.currentImageIndex--;
    } else {
      this.currentImageIndex = this.allImages.length - 1;
    }
  }

  nextImage(): void {
    if (this.currentImageIndex < this.allImages.length - 1) {
      this.currentImageIndex++;
    } else {
      this.currentImageIndex = 0;
    }
  }

  selectImage(index: number): void {
    this.currentImageIndex = index;
  }

  openGalleryModal(): void {
    this.showGalleryModal = true;
    document.body.style.overflow = 'hidden'; // Previene scroll del body
  }

  closeGalleryModal(): void {
    this.showGalleryModal = false;
    document.body.style.overflow = 'auto';
  }

  goBack(): void {
    this.router.navigate(['/accommodations']);
  }

  makeReservation(): void {
    if (this.accommodation) {
      // Por ahora, solo alerta
      // Después redirigirá a la página de reserva
      alert(`Próximamente: Reservar ${this.accommodation.title}`);
      // this.router.navigate(['/reservations/new'], {
      //   queryParams: { accommodationId: this.accommodation.id }
      // });
    }
  }

  contactHost(): void {
    alert('Próximamente: Contactar al anfitrión');
  }

  shareAccommodation(): void {
    const url = window.location.href;
    navigator.clipboard.writeText(url).then(() => {
      alert('¡Link copiado al portapapeles!');
    });
  }

  get hasAmenities(): boolean {
    return !!(this.accommodation?.amenities && this.accommodation.amenities.length > 0);
  }

  get capacityText(): string {
    if (!this.accommodation) return '';
    const capacity = this.accommodation.capacity;
    return capacity === 1 ? '1 huésped' : `${capacity} huéspedes`;
  }

  getAmenityIcon(amenityName: string): string {
    return AccommodationUtils.getAmenityIcon(amenityName);
  }
}
