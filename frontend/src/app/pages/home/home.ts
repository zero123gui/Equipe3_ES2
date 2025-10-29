import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router, RouterModule } from '@angular/router'; // Adicione RouterModule
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';
import { Evento } from '../../models/models'; // Importa a interface real
import { Api } from '../../services/api'; // Importa o serviço de API
import { TalksModal } from '../../components/talks-modal/talks-modal';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, TalksModal], // Adicione aqui  
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit {
  
  @ViewChild('carouselContent') carouselContent!: ElementRef;
  
  public eventos: Evento[] = [];
  public eventoDestaque: Evento | null = null;
  public isLoggedIn: boolean = false;
  public isAdmin: boolean = false;
  public selectedEventForModal: Evento | null = null;

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

  inscrever(evento: Evento): void { // Recebe o objeto Evento agora
    if (this.isLoggedIn) {
      // TODO: Chamar endpoint real de inscrição
      alert(`Inscrição no evento "${evento.nomeEvento}" realizada com sucesso! (Simulação)`);
      // Simula a atualização da lista de inscritos
      this.authService.isUserRegisteredForEvent(evento.id);
    } else {
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

 isRegistered(eventId: number): boolean {
  // CORREÇÃO: Chame o método público do serviço, em vez de acessar a propriedade
  return this.authService.isUserRegisteredForEvent(eventId);
}

  openTalksModal(evento: Evento): void {
  this.selectedEventForModal = evento;
  }

  closeTalksModal(): void {
    this.selectedEventForModal = null;
  }
}