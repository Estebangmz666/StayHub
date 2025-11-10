export class JwtUtils {

  static decodeToken(token: string): any {
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload);
      return JSON.parse(decoded);
    } catch (error) {
      console.error('Error decodificando token:', error);
      return null;
    }
  }

  static getUserIdFromToken(): string | null {
    const token = localStorage.getItem('authToken');
    if (!token) return null;
    const decoded = this.decodeToken(token);
    if (!decoded) return null;

    return decoded.userId || decoded.sub || decoded.id || decoded.user_id || null;
  }
  
  static getUserInfoFromToken(): {
    userId?: string;
    email?: string;
    role?: string;
    name?: string;
  } | null {
    const token = localStorage.getItem('authToken');
    if (!token) return null;

    const decoded = this.decodeToken(token);
    if (!decoded) return null;

    return {
      userId: decoded.userId || decoded.sub || decoded.id,
      email: decoded.email || decoded.username,
      role: decoded.role || decoded.authorities,
      name: decoded.name
    };
  }

  // Verifica si el token está expirado
  static isTokenExpired(token: string): boolean {
    try {
      const decoded = this.decodeToken(token);
      if (!decoded || !decoded.exp) return true;

      // exp viene en segundos, Date.now() en milisegundos
      const expirationDate = decoded.exp * 1000;
      return Date.now() >= expirationDate;
    } catch {
      return true;
    }
  }
}
