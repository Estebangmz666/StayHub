import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth-service';
import { UserService, User} from '../../../services/user-service';
import {JwtUtils} from '../../../utils/JwtUtils';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  user: User | null = null;
  userName: string = 'Usuario';
  userEmail: string = '';
  userRole: string = '';

  isLoadingUser: boolean = true;
  isSidebarOpen: boolean = false;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
  }

  loadUserInfo(): void {
    this.isLoadingUser = true;
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.user = user;
        this.userName = user.name;
        this.userEmail = user.email;
        this.userRole = user.role;
        this.isLoadingUser = false;

        console.log('Usuario cargado:', user);
      },
      error: (error) => {
        console.error('Error cargando usuario:', error);
        this.isLoadingUser = false;
        this.loadUserFromToken();
      }
    });
  }

  loadUserFromToken(): void {
    const userInfo = JwtUtils.getUserInfoFromToken();
    if (userInfo) {
      this.userName = userInfo.name || 'Usuario';
      this.userEmail = userInfo.email || '';
      this.userRole = userInfo.role || '';
    }
  }

  toggleSidebar(): void {
    this.isSidebarOpen = !this.isSidebarOpen;
  }

  handleLogout(): void {
    if (confirm('¿Estás seguro que deseas cerrar sesión?')) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

  goToProperties(): void {
    this.router.navigate(['/accommodations']);
  }

  goToReservations(): void {
    this.router.navigate(['/accommodations']);
  }

  goToProfile(): void {
    alert('Próximamente: Mi Perfil');
  }

  getUserInitials(): string {
    if (!this.userName) return 'U';

    const names = this.userName.split(' ');
    if (names.length >= 2) {
      return (names[0][0] + names[1][0]).toUpperCase();
    }
    return this.userName.substring(0, 2).toUpperCase();
  }
}
