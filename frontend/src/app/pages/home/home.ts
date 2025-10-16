import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router, RouterModule } from '@angular/router'; // Adicione RouterModule
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';
import { Evento } from '../../models/models'; // Importa a interface real
import { Api } from '../../services/api'; // Importa o serviço de API

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule], // Adicione RouterModule para routerLink funcionar
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {

  @ViewChild('carouselContent') carouselContent!: ElementRef;

  public eventos: Evento[] = [];
  public eventoDestaque: Evento | null = null;
  public isLoggedIn: boolean = false;
  public isAdmin: boolean = false;

  constructor(
    private router: Router, 
    private authService: AuthService,
    private apiService: Api // Injeta o ApiService
  ) {}

  ngOnInit(): void {
    this.carregarEventos();
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.isAdmin = this.authService.isAdmin(); // Verifica se é admin
    });
  }

  carregarEventos(): void {
    this.apiService.getEventos(0, 10).subscribe(page => { 
      this.eventos = page.content; 
      // Define o primeiro evento da lista como destaque
      if (this.eventos.length > 0) {
        this.eventoDestaque = this.eventos[0];
      }
    });
  }

  inscrever(eventoId: number): void {
    if (this.isLoggedIn) {
      // Lógica de inscrição:
      // TODO: Chamar o endpoint /event-registrations quando ele for fornecido.
      alert(`Inscrição no evento ${eventoId} realizada com sucesso! (Simulação)`);
    } else {
      // Redireciona para o login se não estiver logado
      this.router.navigate(['/login']);
    }
  }

  scroll(direction: 'left' | 'right') {
    const scrollAmount = 300; // Ajuste o valor do scroll
    if (direction === 'left') {
      this.carouselContent.nativeElement.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    } else {
      this.carouselContent.nativeElement.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  }
}