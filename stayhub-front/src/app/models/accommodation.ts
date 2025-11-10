export interface AccommodationRequestDTO {
  title: string;
  description: string;
  capacity: number;
  mainImage: string;
  longitude: number;
  latitude: number;
  locationDescription: string;
  city: string;
  pricePerNight: number;
  images: string[];
  amenityIds?: number[];
}

export interface AccommodationUpdateDTO {
  title?: string;
  description?: string;
  capacity?: number;
  mainImage?: string;
  longitude?: number;
  latitude?: number;
  locationDescription?: string;
  city?: string;
  pricePerNight?: number;
  images?: string[];
  amenityIds?: number[];
}

export interface AccommodationResponseDTO {
  id: number;
  title: string;
  description: string;
  capacity: number;
  mainImage: string;
  longitude: number;
  latitude: number;
  locationDescription: string;
  city: string;
  pricePerNight: number;
  images: string[];
  amenities?: Amenity[];
  hostName?: string;
  hostId?: number;
}

export interface Amenity {
  id: number;
  name: string;
  icon?: string;
}

export interface SearchResponseDTO<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
}

export interface AccommodationFilters {
  city?: string;
  minCapacity?: number;
  maxPrice?: number;
  amenityIds?: number[];
  page?: number;
  size?: number;
}
