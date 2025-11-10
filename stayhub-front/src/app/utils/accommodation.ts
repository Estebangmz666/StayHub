import {ACCOMMODATION_CONSTANTS} from '../constants/accommodation';

export class AccommodationUtils {

  static formatPrice(price: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(price);
  }

  static calculateTotalPrice(pricePerNight: number, nights: number): number {
    return pricePerNight * nights;
  }

  static getAmenityIcon(amenityName: string): string {
    const icons = ACCOMMODATION_CONSTANTS.AMENITY_ICONS;
    return icons[amenityName as keyof typeof icons] || '✓';
  }

  static truncateText(text: string, maxLength: number): string {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  }

  static isValidImageUrl(url: string): boolean {
    try {
      const urlObj = new URL(url);
      return /\.(jpg|jpeg|png|webp|gif)$/i.test(urlObj.pathname);
    } catch {
      return false;
    }
  }
}
