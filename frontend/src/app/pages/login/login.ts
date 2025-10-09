import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  // Injeta o serviço de roteamento
  constructor(private router: Router) {}

  login() {
    // TODO: Aqui você faria a chamada para a sua API de back-end para validar o login
    console.log('Tentativa de login...');

    // Simula um login bem-sucedido e navega para a página principal
    this.router.navigate(['/home']);
  }

  goToRegister() {
    // Navega para a página de registro
    this.router.navigate(['/register']);
  }
}
