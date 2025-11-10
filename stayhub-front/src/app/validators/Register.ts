export class RegisterValidators {

  static isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  static isValidPassword(password: string): boolean {
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
    return passwordRegex.test(password);
  }

  static isValidPhoneNumber(phone: string): boolean {
    const phoneRegex = /^\+57\s?\d{10}$/;
    return phoneRegex.test(phone);
  }

  static isValidBirthDate(dateString: string): { valid: boolean; message?: string } {
    const date = new Date(dateString);
    const today = new Date();

    if (isNaN(date.getTime())) {
      return { valid: false, message: 'Fecha inválida' };
    }

    if (date >= today) {
      return { valid: false, message: 'La fecha debe ser en el pasado' };
    }

    let age = today.getFullYear() - date.getFullYear();
    const monthDiff = today.getMonth() - date.getMonth();
    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < date.getDate())) {
      age--;
    }

    if (age < 18) {
      return { valid: false, message: 'Debes ser mayor de 18 años' };
    }

    return { valid: true };
  }

  static isValidUrl(url: string): boolean {
    try {
      new URL(url);
      return true;
    } catch {
      return false;
    }
  }

  static formatPhoneNumber(input: string): string {
    let cleaned = input.replace(/\D/g, '');

    if (cleaned.startsWith('57')) {
      cleaned = '+' + cleaned;
    }

    if (!cleaned.startsWith('+57')) {
      cleaned = '+57' + cleaned;
    }

    if (cleaned.length > 13) {
      cleaned = cleaned.substring(0, 13);
    }

    if (cleaned.length > 3) {
      cleaned = cleaned.substring(0, 3) + ' ' + cleaned.substring(3);
    }

    return cleaned;
  }
}
