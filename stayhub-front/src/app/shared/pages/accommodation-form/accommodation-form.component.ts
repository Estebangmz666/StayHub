import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AccommodationService } from '../../../services/accommodation-service';
import { AuthService } from '../../../services/auth-service';
import {
  AccommodationRequestDTO,
  AccommodationUpdateDTO,
} from '../../../models/accommodation';
import { ACCOMMODATION_CONSTANTS } from '../../../constants/accommodation';
import {FormsModule} from '@angular/forms';

interface FormData {
  title: string;
  description: string;
  capacity: number;
  city: string;
  locationDescription: string;
  latitude: number;
  longitude: number;
  pricePerNight: number;
  mainImage: string;
  images: string[];
  amenityIds: number[];
}

interface FormErrors {
  title?: string;
  description?: string;
  capacity?: string;
  city?: string;
  locationDescription?: string;
  latitude?: string;
  longitude?: string;
  pricePerNight?: string;
  mainImage?: string;
  images?: string;
}

@Component({
  selector: 'app-accommodation-form',
  standalone: true,
  templateUrl: './accommodation-form.component.html',
  imports: [
    FormsModule
  ],
  styleUrls: ['./accommodation-form.component.css']
})
export class AccommodationFormComponent implements OnInit {
  // Modo: 'create' o 'edit'
  mode: 'create' | 'edit' = 'create';
  accommodationId: number | null = null;

  // Formulario
  formData: FormData = {
    title: '',
    description: '',
    capacity: 1,
    city: '',
    locationDescription: '',
    latitude: 0,
    longitude: 0,
    pricePerNight: 0,
    mainImage: '',
    images: [],
    amenityIds: []
  };

  newImageUrl: string = '';

  errors: FormErrors = {};

  isLoading = false;
  isSubmitting = false;
  error: string = '';

  cities = ACCOMMODATION_CONSTANTS.CITIES;
  username: string = '';

  availableAmenities = [
    { id: 1, name: 'WiFi', icon: '📶' },
    { id: 2, name: 'Piscina', icon: '🏊' },
    { id: 3, name: 'Parqueadero', icon: '🚗' },
    { id: 4, name: 'Cocina', icon: '🍳' },
    { id: 5, name: 'TV', icon: '📺' },
    { id: 6, name: 'Aire Acondicionado', icon: '❄️' },
    { id: 7, name: 'Lavadora', icon: '🧺' },
    { id: 8, name: 'Gym', icon: '🏋️' },
    { id: 9, name: 'Mascotas permitidas', icon: '🐕' },
    { id: 10, name: 'Seguridad 24/7', icon: '🔒' }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accommodationService: AccommodationService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    // Obtiene username
    const userInfo = this.authService.getCurrentUserInfo();
    if (userInfo?.email) {
      this.username = userInfo.email;
    } else {
      this.error = 'No se pudo obtener información del usuario';
      return;
    }

    // Determina si es create o edit según la URL
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.mode = 'edit';
        this.accommodationId = +params['id'];
        this.loadAccommodation(this.accommodationId);
      } else {
        this.mode = 'create';
      }
    });
  }

  // Carga la propiedad para editar
  loadAccommodation(id: number): void {
    this.isLoading = true;

    this.accommodationService.getAccommodation(id).subscribe({
      next: (accommodation) => {
        this.formData = {
          title: accommodation.title,
          description: accommodation.description,
          capacity: accommodation.capacity,
          city: accommodation.city,
          locationDescription: accommodation.locationDescription,
          latitude: accommodation.latitude,
          longitude: accommodation.longitude,
          pricePerNight: accommodation.pricePerNight,
          mainImage: accommodation.mainImage,
          images: accommodation.images || [],
          amenityIds: accommodation.amenities?.map(a => a.id) || []
        };
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando propiedad:', error);
        this.error = error.message;
        this.isLoading = false;
      }
    });
  }

  // Validaciones
  validateForm(): boolean {
    this.errors = {};

    if (!this.formData.title || this.formData.title.trim().length < 5) {
      this.errors.title = 'El título debe tener al menos 5 caracteres';
    }

    if (!this.formData.description || this.formData.description.trim().length < 20) {
      this.errors.description = 'La descripción debe tener al menos 20 caracteres';
    }

    if (this.formData.capacity < 1 || this.formData.capacity > 20) {
      this.errors.capacity = 'La capacidad debe estar entre 1 y 20 huéspedes';
    }

    if (!this.formData.city) {
      this.errors.city = 'Selecciona una ciudad';
    }

    if (!this.formData.locationDescription || this.formData.locationDescription.trim().length < 10) {
      this.errors.locationDescription = 'Describe mejor la ubicación (mínimo 10 caracteres)';
    }

    if (this.formData.latitude < -90 || this.formData.latitude > 90) {
      this.errors.latitude = 'Latitud inválida (debe estar entre -90 y 90)';
    }

    if (this.formData.longitude < -180 || this.formData.longitude > 180) {
      this.errors.longitude = 'Longitud inválida (debe estar entre -180 y 180)';
    }

    if (this.formData.pricePerNight <= 0) {
      this.errors.pricePerNight = 'El precio debe ser mayor a 0';
    }

    if (!this.formData.mainImage || !this.isValidUrl(this.formData.mainImage)) {
      this.errors.mainImage = 'Ingresa una URL válida para la imagen principal';
    }

    return Object.keys(this.errors).length === 0;
  }

  isValidUrl(url: string): boolean {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  }

  // Maneja amenidades (checkbox)
  toggleAmenity(amenityId: number): void {
    const index = this.formData.amenityIds.indexOf(amenityId);
    if (index > -1) {
      this.formData.amenityIds.splice(index, 1);
    } else {
      this.formData.amenityIds.push(amenityId);
    }
  }

  isAmenitySelected(amenityId: number): boolean {
    return this.formData.amenityIds.includes(amenityId);
  }

  // Agregar imagen adicional
  addImage(): void {
    if (this.newImageUrl && this.isValidUrl(this.newImageUrl)) {
      if (this.formData.images.length < ACCOMMODATION_CONSTANTS.MAX_IMAGES) {
        this.formData.images.push(this.newImageUrl);
        this.newImageUrl = '';
      } else {
        alert(`Máximo ${ACCOMMODATION_CONSTANTS.MAX_IMAGES} imágenes`);
      }
    } else {
      alert('Ingresa una URL válida');
    }
  }

  // Eliminar imagen adicional
  removeImage(index: number): void {
    this.formData.images.splice(index, 1);
  }

  // Enviar formulario
  handleSubmit(): void {
    if (!this.validateForm()) {
      alert('Por favor corrige los errores en el formulario');
      return;
    }

    this.isSubmitting = true;

    if (this.mode === 'create') {
      this.createAccommodation();
    } else {
      this.updateAccommodation();
    }
  }

  createAccommodation(): void {
    const data: AccommodationRequestDTO = {
      ...this.formData
    };

    this.accommodationService.createAccommodation(data, this.username).subscribe({
      next: (response) => {
        console.log('Propiedad creada:', response);
        alert('¡Propiedad creada exitosamente! 🎉');
        this.router.navigate(['/my-accommodations']);
      },
      error: (error) => {
        console.error('Error creando propiedad:', error);
        alert(`Error: ${error.message}`);
        this.isSubmitting = false;
      }
    });
  }

  updateAccommodation(): void {
    if (!this.accommodationId) return;

    const data: AccommodationUpdateDTO = {
      ...this.formData
    };

    this.accommodationService.updateAccommodation(this.accommodationId, data, this.username).subscribe({
      next: (response) => {
        console.log('Propiedad actualizada:', response);
        alert('¡Propiedad actualizada exitosamente! ✅');
        this.router.navigate(['/my-accommodations']);
      },
      error: (error) => {
        console.error('Error actualizando propiedad:', error);
        alert(`Error: ${error.message}`);
        this.isSubmitting = false;
      }
    });
  }

  // Cancelar
  cancel(): void {
    if (confirm('¿Seguro que quieres cancelar? Los cambios no guardados se perderán.')) {
      this.router.navigate(['/my-accommodations']);
    }
  }

  // Helpers
  get pageTitle(): string {
    return this.mode === 'create' ? 'Crear Nueva Propiedad' : 'Editar Propiedad';
  }

  get submitButtonText(): string {
    if (this.isSubmitting) {
      return this.mode === 'create' ? 'Creando...' : 'Guardando...';
    }
    return this.mode === 'create' ? 'Crear Propiedad' : 'Guardar Cambios';
  }

  protected readonly ACCOMMODATION_CONSTANTS = ACCOMMODATION_CONSTANTS;
}
