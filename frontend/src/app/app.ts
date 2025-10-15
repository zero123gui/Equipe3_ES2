import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './pages/header/header'; // <-- IMPORTE O HEADER

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Header], // <-- ADICIONE AQUI
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = 'cadastro-app';
}