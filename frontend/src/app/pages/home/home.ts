import { Component, ElementRef, OnInit, ViewChild, OnDestroy } from '@angular/core'; // Adicione OnDestroy
import { Router, RouterModule } from '@angular/router'; // Adicione RouterModule
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common';
import { Evento } from '../../models/models'; // Importa a interface real
import { Api } from '../../services/api'; // Importa o serviço de API
import { TalksModal } from '../../components/talks-modal/talks-modal';
import { Subscription } from 'rxjs'; // Importe Subscription

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, TalksModal], // Adicione aqui  
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home implements OnInit, OnDestroy {
  
  @ViewChild('carouselContent') carouselContent!: ElementRef;
  
  public eventos: Evento[] = [];
  public eventoDestaque: Evento | null = null;
  public isLoggedIn: boolean = false;
  public isAdmin: boolean = false;
  public selectedEventForModal: Evento | null = null;

  public registeredEventIds: number[] = [];
  private registrationSub!: Subscription;

  constructor(
    private router: Router, 
    private authService: AuthService,
    private apiService: Api // Injeta o ApiService
  ) {}

  ngOnInit(): void {
    this.carregarEventos();
    this.authService.isLoggedIn$.subscribe((status: boolean) => {
      this.isLoggedIn = status;
      this.isAdmin = this.authService.isAdmin();
    });

    // --- MUDANÇA: Se inscreve na lista de IDs do AuthService ---
    this.registrationSub = this.authService.registeredEventIds$.subscribe(ids => {
      this.registeredEventIds = ids; // Atualiza nossa lista local
    });
  }

  ngOnDestroy(): void {
    // --- MUDANÇA: Limpa a inscrição para evitar vazamento de memória ---
    if (this.registrationSub) {
      this.registrationSub.unsubscribe();
    }
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

  inscrever(evento: Evento): void {
  if (this.isLoggedIn) {
    // Agora estamos chamando o método REAL no AuthService
    this.authService.registerForEvent(evento.id).subscribe({
      next: () => {
        // A lista vai atualizar sozinha por causa do 'tap' no serviço.
        // O BehaviorSubject vai notificar o componente e o botão mudará.
        alert(`Inscrição no evento "${evento.nomeEvento}" realizada com sucesso!`);
      },
      error: (err: any) => {
        console.error("Erro ao se inscrever no evento:", err);
        // O back-end provavelmente retorna 409 Conflict se já inscrito
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

  scroll(direction: 'left' | 'right') {
    const scrollAmount = 300; // Ajuste o valor do scroll
    if (direction === 'left') {
      this.carouselContent.nativeElement.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    } else {
      this.carouselContent.nativeElement.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  }

 isRegistered(eventId: number): boolean {
  return this.authService.isUserRegisteredForEvent(eventId);
  }

  openTalksModal(evento: Evento): void {
  this.selectedEventForModal = evento;
  }

  closeTalksModal(): void {
    this.selectedEventForModal = null;
  }
}