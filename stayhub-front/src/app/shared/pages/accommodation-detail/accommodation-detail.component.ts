import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AccommodationService } from '../../../services/accommodation-service';
import { AccommodationResponseDTO } from '../../../models/accommodation';
import { AccommodationUtils } from '../../../utils/accommodation';
import {RESERVATION_CONSTANTS} from '../../../constants/reservation';
import {ReservationFormData, ReservationRequestDTO} from '../../../models/reservation';
import {ReservationUtils} from '../../../utils/reservation';
import {ReservationService} from '../../../services/reservation-service';
import {AuthService} from '../../../services/auth-service';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-accommodation-detail',
  standalone: true,
  templateUrl: './accommodation-detail.component.html',
  imports: [
    FormsModule
  ],
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

  reservationErrors: any = {};
  isSubmittingReservation = false;
  reservationUtils = ReservationUtils;
  reservationConstants = RESERVATION_CONSTANTS;

  currentUserId: number | null = null;
  currentUserEmail: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accommodationService: AccommodationService,
    private reservationService: ReservationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const id = +params['id'];
      if (id) {
        this.loadAccommodation(id);
      }
    });
    const userInfo = this.authService.getCurrentUserInfo();
    if (userInfo) {
      this.currentUserEmail = userInfo.email || '';
      this.currentUserId = userInfo.userId ? +userInfo.userId : null;
    }
    this.reservationForm.checkInDate = ReservationUtils.getMinCheckInDate();
    this.reservationForm.checkOutDate = ReservationUtils.getMinCheckOutDate();
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

  reservationForm: ReservationFormData = {
    checkInDate: '',
    checkInTime: RESERVATION_CONSTANTS.DEFAULT_CHECK_IN_TIME,
    checkOutDate: '',
    checkOutTime: RESERVATION_CONSTANTS.DEFAULT_CHECK_OUT_TIME,
    numberOfGuests: 1
  };

  validateReservationForm(): boolean {
    this.reservationErrors = {};

    if (!this.reservationForm.checkInDate) {
      this.reservationErrors.checkInDate = 'Selecciona la fecha de entrada';
    }

    if (!this.reservationForm.checkOutDate) {
      this.reservationErrors.checkOutDate = 'Selecciona la fecha de salida';
    }

    const checkInDateTime = ReservationUtils.combineDateTime(
      this.reservationForm.checkInDate,
      this.reservationForm.checkInTime
    );
    const checkOutDateTime = ReservationUtils.combineDateTime(
      this.reservationForm.checkOutDate,
      this.reservationForm.checkOutTime
    );

    if (!ReservationUtils.isValidDateRange(checkInDateTime, checkOutDateTime)) {
      this.reservationErrors.checkOutDate = 'La fecha de salida debe ser posterior a la entrada';
    }

    if (this.reservationForm.numberOfGuests < 1) {
      this.reservationErrors.numberOfGuests = 'Mínimo 1 huésped';
    }

    if (this.accommodation && this.reservationForm.numberOfGuests > this.accommodation.capacity) {
      this.reservationErrors.numberOfGuests = `Máximo ${this.accommodation.capacity} huéspedes`;
    }

    return Object.keys(this.reservationErrors).length === 0;
  }

  makeReservation(): void {
    if (!this.validateReservationForm()) {
      alert('Por favor corrige los errores en el formulario');
      return;
    }

    if (!this.currentUserId) {
      alert('Debes iniciar sesión para hacer una reserva');
      this.router.navigate(['/login']);
      return;
    }

    if (!this.accommodation) return;

    this.isSubmittingReservation = true;

    const checkInDateTime = ReservationUtils.combineDateTime(
      this.reservationForm.checkInDate,
      this.reservationForm.checkInTime
    );
    const checkOutDateTime = ReservationUtils.combineDateTime(
      this.reservationForm.checkOutDate,
      this.reservationForm.checkOutTime
    );

    const reservationData: ReservationRequestDTO = {
      guestId: this.currentUserId,
      accommodationId: this.accommodation.id,
      checkInDate: checkInDateTime,
      checkOutDate: checkOutDateTime,
      numberOfGuests: this.reservationForm.numberOfGuests
    };

    this.reservationService.createReservation(reservationData, this.currentUserEmail)
      .subscribe({
        next: (response) => {
          console.log('Reserva creada:', response);
          alert(`¡Reserva creada exitosamente! 🎉\n\nEstado: ${ReservationUtils.translateStatus(response.status)}\nTotal: ${ReservationUtils.formatPrice(response.totalPrice)}`);
          this.router.navigate(['/my-reservations']);
        },
        error: (error) => {
          console.error('Error creando reserva:', error);
          alert(`Error: ${error.message}`);
          this.isSubmittingReservation = false;
        }
      });
  }

  onCheckInChange(): void {
    if (this.reservationForm.checkInDate) {
      const minCheckOut = ReservationUtils.getMinCheckOutDate(this.reservationForm.checkInDate);
      if (this.reservationForm.checkOutDate < minCheckOut) {
        this.reservationForm.checkOutDate = minCheckOut;
      }
    }
  }

  get numberOfNights(): number {
    if (this.reservationForm.checkInDate && this.reservationForm.checkOutDate) {
      const checkIn = ReservationUtils.combineDateTime(
        this.reservationForm.checkInDate,
        this.reservationForm.checkInTime
      );
      const checkOut = ReservationUtils.combineDateTime(
        this.reservationForm.checkOutDate,
        this.reservationForm.checkOutTime
      );
      return ReservationUtils.calculateNights(checkIn, checkOut);
    }
    return 0;
  }

  get calculatedTotalPrice(): number {
    if (this.accommodation && this.numberOfNights > 0) {
      return ReservationUtils.calculateTotalPrice(
        this.accommodation.pricePerNight,
        this.numberOfNights
      );
    }
    return 0;
  }
 }
