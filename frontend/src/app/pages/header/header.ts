import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router'; // Importe RouterModule
import { CommonModule } from '@angular/common'; // Importe CommonModule
import { AuthService } from '../../services/auth'; // Vamos criar este serviço

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule], // Adicione CommonModule e RouterModule
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header implements OnInit {
  isLoggedIn: boolean = false;
  showUserMenu: boolean = false; // Para um possível menu de usuário

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // Assina as mudanças no status de login
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
    });
  }

  toggleUserMenu() {
    this.showUserMenu = !this.showUserMenu;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
    this.showUserMenu = false;
  }
}