import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  constructor(private router: Router) {}

  createAccount() {
    // TODO: Aqui você faria a chamada para a sua API de back-end para criar a conta
    console.log('Tentativa de criar conta...');

    // Simula a criação bem-sucedida e navega para a tela de sucesso
    this.router.navigate(['/register-success']);
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
