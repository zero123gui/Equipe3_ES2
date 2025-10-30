import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Evento, Palestra } from '../../models/models';
import { Api } from '../../services/api';
import { AuthService } from '../../services/auth';
import { PalestraForm } from '../palestra-form/palestra-form'; // <-- IMPORTE O NOVO FORM

@Component({
  selector: 'app-talks-modal',
  standalone: true,
  imports: [CommonModule, PalestraForm], // <-- ADICIONE AQUI
  templateUrl: './talks-modal.html',
  styleUrl: './talks-modal.css'
})
export class TalksModal implements OnChanges {
  @Input() evento: Evento | null = null;
  @Output() closeModal = new EventEmitter<void>();

  public palestras: Palestra[] = [];
  public isLoading: boolean = false;
  private isLoggedIn: boolean = false;

  public isAdmin: boolean = false; // <-- ADICIONE
  public showCreateForm: boolean = false; // <-- ADICIONE

  constructor(
    private apiService: Api,
    private authService: AuthService,
    private router: Router
  ) {
    // Verifica o status de login e admin
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.isAdmin = this.authService.isAdmin(); // <-- CORREÇÃO: Agora atualiza junto com o login
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['evento'] && this.evento) {
      this.carregarPalestras(this.evento.id);
    }
  }

  carregarPalestras(idEvento: number): void {
    this.isLoading = true;
    this.palestras = []; 
    this.apiService.getPalestrasByEventoId(idEvento).subscribe({
      next: (data) => {
        this.palestras = data;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error("Erro ao carregar palestras:", err);
        this.isLoading = false;
      }
    });
  }

  // --- NOVO MÉTODO ---
  // Chamado quando o formulário emite 'palestraCriada'
  onPalestraCreated(): void {
    this.showCreateForm = false; // Fecha o formulário
    this.carregarPalestras(this.evento!.id); // Recarrega a lista de palestras
  }

  inscreverNoEvento(): void {
    if (!this.evento) return;

    if (this.isLoggedIn) {
      this.authService.registerForEvent(this.evento.id).subscribe({
        next: () => {
          alert(`Inscrição no evento "${this.evento!.nomeEvento}" realizada com sucesso!`);
          this.onClose(); // Fecha o modal após a inscrição
        },
        error: (err: any) => {
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

  onClose(): void {
    this.closeModal.emit();
  }
}