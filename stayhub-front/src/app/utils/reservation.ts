import {ReservationStatus} from '../models/reservation';

export class ReservationUtils {

  // Combina fecha y hora en formato ISO 8601
  static combineDateTime(date: string, time: string): string {
    // date: "2025-11-20"
    // time: "15:00"
    // resultado: "2025-11-20T15:00:00"
    return `${date}T${time}:00`;
  }

  // Separa fecha y hora de un string ISO
  static splitDateTime(dateTime: string): { date: string; time: string } {
    // dateTime: "2025-11-20T15:00:00"
    const [date, timeWithSeconds] = dateTime.split('T');
    const time = timeWithSeconds.substring(0, 5); // "15:00"
    return { date, time };
  }

  // Calcula el número de noches entre dos fechas
  static calculateNights(checkIn: string, checkOut: string): number {
    const start = new Date(checkIn);
    const end = new Date(checkOut);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  }

  // Calcula el precio total
  static calculateTotalPrice(pricePerNight: number, nights: number): number {
    return pricePerNight * nights;
  }

  // Formatea el precio
  static formatPrice(price: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(price);
  }

  // Formatea una fecha para mostrar
  static formatDate(dateString: string): string {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }).format(date);
  }

  // Formatea fecha y hora
  static formatDateTime(dateString: string): string {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('es-CO', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  }

  // Traduce el estado al español
  static translateStatus(status: ReservationStatus): string {
    const translations = {
      [ReservationStatus.PENDING]: 'Pendiente',
      [ReservationStatus.CONFIRMED]: 'Confirmada',
      [ReservationStatus.CANCELLED]: 'Cancelada'
    };
    return translations[status];
  }

  // Obtiene la clase CSS según el estado
  static getStatusClass(status: ReservationStatus): string {
    const classes = {
      [ReservationStatus.PENDING]: 'status-pending',
      [ReservationStatus.CONFIRMED]: 'status-confirmed',
      [ReservationStatus.CANCELLED]: 'status-cancelled'
    };
    return classes[status];
  }

  // Valida que check-out sea después de check-in
  static isValidDateRange(checkIn: string, checkOut: string): boolean {
    const start = new Date(checkIn);
    const end = new Date(checkOut);
    return end > start;
  }

  // Válida que la fecha sea futura
  static isFutureDate(dateString: string): boolean {
    const date = new Date(dateString);
    const now = new Date();
    return date > now;
  }

  // Obtiene la fecha mínima para check-in (hoy)
  static getMinCheckInDate(): string {
    const today = new Date();
    return today.toISOString().split('T')[0]; // "2025-11-15"
  }

  // Obtiene la fecha mínima para check-out (mañana)
  static getMinCheckOutDate(checkInDate?: string): string {
    const baseDate = checkInDate ? new Date(checkInDate) : new Date();
    const tomorrow = new Date(baseDate);
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow.toISOString().split('T')[0];
  }
}
