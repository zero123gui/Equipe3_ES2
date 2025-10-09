import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api'; // <-- IMPORTE O SERVIÇO

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  public credentials = {
    email: '',
    password: ''
  };

  public errorMessage = '';

  constructor(private router: Router, private apiService: ApiService) {}

  login() {
    this.errorMessage = '';

    this.apiService.login(this.credentials).subscribe(success => {
      if (success) {
        // TRUE: Deu certo, acessa a conta
        this.router.navigate(['/home']);
      } else {
        // FALSE: Deu errado, limpa os campos e exibe mensagem
        this.errorMessage = 'Email ou senha inválidos.';
        this.credentials.email = '';
        this.credentials.password = '';
      }
    });
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }
}