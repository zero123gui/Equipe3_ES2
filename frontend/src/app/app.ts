import { Component, OnInit } from '@angular/core'; // Adicione OnInit
import { RouterOutlet } from '@angular/router';
import { Header } from './pages/header/header';
import { AuthService } from './services/auth'; // <-- IMPORTE O AUTH SERVICE

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Header],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit { // <-- Implemente OnInit
  title = 'cadastro-app';

  constructor(private authService: AuthService) {} // <-- INJETE O AUTH SERVICE

  ngOnInit(): void {
    // Se o usuário recarregar a página e já tiver um token,
    // carregamos suas inscrições.
    if (this.authService.hasToken()) { // hasToken() é público? Se não, mude no auth.service.ts
      this.authService.loadMyRegistrations();
    }
  }
}