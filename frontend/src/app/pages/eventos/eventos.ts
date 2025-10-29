import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router'; // Import Router
import { Evento } from '../../models/models';
import { Api } from '../../services/api';
import { AuthService } from '../../services/auth'; // Import AuthService
import { TalksModal } from '../../components/talks-modal/talks-modal';

@Component({
  selector: 'app-eventos',
  standalone: true,
  imports: [CommonModule, RouterModule, TalksModal], // Adicione aqui  
  templateUrl: './eventos.html',
  styleUrl: './eventos.css'
})
export class Eventos implements OnInit {

  public eventos: Evento[] = [];
  public isLoading: boolean = true; // Para mostrar um feedback de carregamento
  public selectedEventForModal: Evento | null = null;
  private isLoggedIn: boolean = false; // Add isLoggedIn property

  constructor(
    private apiService: Api,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarTodosEventos();
  }

  carregarTodosEventos(): void {
    this.isLoading = true;
    // Buscamos um número grande para garantir que todos os eventos sejam listados
    // Em um app real, aqui implementaríamos a paginação completa
    this.apiService.getEventos(0, 50).subscribe({
      next: (page) => {
        this.eventos = page.content;
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Erro ao carregar eventos:", err);
        this.isLoading = false;
      }
    });
  }

  inscrever(eventoId: number): void {
    if (this.isLoggedIn) {
      // TODO: Call the real registration endpoint when available.
      alert(`Inscrição no evento ${eventoId} realizada com sucesso! (Simulação)`);
      this.authService.isUserRegisteredForEvent(eventoId);
    } else {
      // Redirect to the login page if not logged in
      this.router.navigate(['/login']);
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