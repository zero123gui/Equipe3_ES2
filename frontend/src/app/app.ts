import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
// CORREÇÃO: O nome da classe é 'HeaderComponent' e o caminho padrão é 'components/header/header.component'
// (Ajuste o caminho se o seu for 'pages/header/header.component')
import { Header } from './pages/header/header'; 
import { AuthService } from './services/auth'; // CORREÇÃO: O arquivo se chama auth.service.ts

@Component({
  selector: 'app-root',
  standalone: true,
  // CORREÇÃO: Use o nome da classe importada: HeaderComponent
  imports: [RouterOutlet, Header], 
  templateUrl: './app.html', // Corrija se o seu for 'app.html'
  styleUrl: './app.css' // Corrija se o seu for 'app.css'
})
// CORREÇÃO: O nome da classe é 'AppComponent'
export class App implements OnInit { 
  title = 'cadastro-app';

  constructor(private authService: AuthService) {} 

  ngOnInit(): void {
    // Esta é a sua correção da dependência circular, que está correta.
    if (this.authService.hasToken()) {
      this.authService.loadMyRegistrations();
    }
  }
}