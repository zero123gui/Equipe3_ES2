import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Evento, Palestra } from '../../models/models';
import { Api } from '../../services/api';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-talks-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './talks-modal.html',
  styleUrl: './talks-modal.css'
})
export class TalksModal implements OnChanges {
  // Recebe o evento do componente pai (Home ou Eventos)
  @Input() evento: Evento | null = null;
  // Emite um evento para avisar o pai que o modal deve ser fechado
  @Output() closeModal = new EventEmitter<void>();

  public palestras: Palestra[] = [];
  public isLoading: boolean = false;
  private isLoggedIn: boolean = false;

  constructor(
    private apiService: Api,
    private authService: AuthService,
    private router: Router
  ) {
    this.authService.isLoggedIn$.subscribe(status => this.isLoggedIn = status);
  }

  // Este método é chamado sempre que o @Input 'evento' muda
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['evento'] && this.evento) {
      this.carregarPalestras(this.evento.id);
    }
  }

  carregarPalestras(idEvento: number): void {
    this.isLoading = true;
    this.palestras = []; // Limpa a lista anterior
    this.apiService.getPalestrasByEventoId(idEvento).subscribe({
      next: (data) => {
        this.palestras = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error("Erro ao carregar palestras:", err);
        this.isLoading = false;
      }
    });
  }

  inscreverNoEvento(): void {
    if (!this.evento) return;

    if (this.isLoggedIn) {
      // TODO: Chamar o endpoint real de inscrição quando fornecido.
      // Por enquanto, vamos usar o alert de simulação.
      alert(`Inscrição no evento "${this.evento.nomeEvento}" realizada com sucesso! (Simulação)`);
      this.onClose(); // Fecha o modal após a inscrição
    } else {
      this.router.navigate(['/login']);
    }
  }

  // Emite o evento para fechar o modal
  onClose(): void {
    this.closeModal.emit();
  }
}