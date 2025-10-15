import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms'; 
import { ApiService } from '../../services/api';
import { AuthService } from '../../services/auth'; // <-- IMPORTE O AUTH SERVICE

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  public errorMessage = '';

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });

  // Injeta o AuthService aqui
  constructor(private router: Router, private apiService: ApiService, private authService: AuthService) {}

  // ... (imports e outras partes da classe)

login() {
  if (this.loginForm.invalid) { return; }
  this.errorMessage = '';

  const credentials = {
    email: this.loginForm.value.email || '',
    senha: this.loginForm.value.password || '' // O back-end espera 'senha', não 'password'
  };

  // MUDANÇA: Chama o método login do AuthService
  this.authService.login(credentials).subscribe({
    next: (response) => {
      // Sucesso! O token já foi salvo pelo serviço.
      console.log('Login bem-sucedido, token salvo.');
      this.router.navigate(['/home']);
    },
    error: (err) => {
      // Tratamento de erro
      if (err.status === 401) {
        this.errorMessage = 'Email ou senha inválidos.';
      } else {
        this.errorMessage = 'Ocorreu um erro no servidor. Tente novamente.';
      }
      this.loginForm.reset();
    }
  });
}

// ... (resto da classe)

  goToRegister() {
    this.router.navigate(['/register']);
  }
}