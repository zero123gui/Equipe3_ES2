import { Component, OnInit, OnDestroy } from '@angular/core'; // Adicione OnDestroy
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { Evento } from '../../models/models';
import { Api } from '../../services/api';
import { AuthService } from '../../services/auth';
import { TalksModal } from '../../components/talks-modal/talks-modal';
import { Subscription } from 'rxjs'; // Importe Subscription

@Component({
  selector: 'app-eventos',
  standalone: true,
  imports: [CommonModule, RouterModule, TalksModal], 
  templateUrl: './eventos.html',
  styleUrl: './eventos.css'
})
export class Eventos implements OnInit, OnDestroy { // Implemente OnDestroy

  public eventos: Evento[] = [];
  public isLoading: boolean = true;
  public selectedEventForModal: Evento | null = null;
  public isLoggedIn: boolean = false;
  
  // Lista local de IDs para verificar o status "Inscrito"
  public registeredEventIds: number[] = [];
  private registrationSub!: Subscription;

  constructor(
    private apiService: Api,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.carregarTodosEventos();
    
    // Se inscreve na lista de IDs do AuthService
    this.registrationSub = this.authService.registeredEventIds$.subscribe(ids => {
      this.registeredEventIds = ids; // Atualiza nossa lista local
    });

    // Também se inscreve no status de login
    this.authService.isLoggedIn$.subscribe((status: boolean) => {
      this.isLoggedIn = status;
    });
  }

  ngOnDestroy(): void {
    // Limpa a inscrição para evitar vazamento de memória
    if (this.registrationSub) {
      this.registrationSub.unsubscribe();
    }
  }

  carregarTodosEventos(): void {
    this.isLoading = true;
    this.apiService.getEventos(0, 50).subscribe({
      next: (page) => {
        this.eventos = page.content;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error("Erro ao carregar eventos:", err);
        this.isLoading = false;
      }
    });
  }

  // --- FUNÇÃO CORRIGIDA ---
  // Recebe 'eventoId: number', exatamente como o seu HTML está enviando
  inscrever(eventoId: number): void { 
    if (this.isLoggedIn) {
      // Chama o serviço de inscrição real
      this.authService.registerForEvent(eventoId).subscribe({
        next: () => {
          // Encontra o nome do evento na nossa lista local para o alerta
          const evento = this.eventos.find(e => e.id === eventoId);
          const nomeEvento = evento ? evento.nomeEvento : `Evento ${eventoId}`;
          alert(`Inscrição no evento "${nomeEvento}" realizada com sucesso!`);
          // A lista de inscritos vai atualizar sozinha (via BehaviorSubject)
        },
        error: (err: any) => {
          console.error("Erro ao se inscrever no evento:", err);
          if (err.status === 409) {
            alert('Você já está inscrito neste evento.');
          } else {
            alert('Ocorreu um erro ao tentar a inscrição.');
          }
        }
      });
    } else {
      this.router.navigate(['/login']);
    }
  }

  // --- FUNÇÃO CORRIGIDA ---
  // Verifica a lista local de IDs
  isRegistered(eventId: number): boolean {
    return this.registeredEventIds.includes(eventId);
  }
  
  openTalksModal(evento: Evento): void {
    this.selectedEventForModal = evento;
  }

  closeTalksModal(): void {
    this.selectedEventForModal = null;
  }
}