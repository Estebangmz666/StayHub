export enum ReservationStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  CANCELLED = 'CANCELLED'
}

export interface ReservationRequestDTO {
  guestId: number;
  accommodationId: number;
  checkInDate: string; // ISO 8601 format: "2025-11-20T15:00:00"
  checkOutDate: string;
  numberOfGuests: number;
}

export interface ReservationResponseDTO {
  id: number;
  guestId: number;
  accommodationTitle: string;
  accommodationId: number;
  checkInDate: string;
  checkOutDate: string;
  numberOfGuests: number;
  totalPrice: number;
  status: ReservationStatus;
  createdAt: string;
  updatedAt: string;
}

export interface ReservationUpdateDTO {
  status: ReservationStatus;
}

export interface ReservationFormData {
  checkInDate: string;
  checkInTime: string;
  checkOutDate: string;
  checkOutTime: string;
  numberOfGuests: number;
}
