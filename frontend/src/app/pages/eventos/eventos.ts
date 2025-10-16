import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Evento } from '../../models/models';
import { Api } from '../../services/api';

@Component({
  selector: 'app-eventos',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './eventos.html',
  styleUrl: './eventos.css'
})
export class Eventos implements OnInit {

  public eventos: Evento[] = [];
  public isLoading: boolean = true; // Para mostrar um feedback de carregamento

  constructor(private apiService: Api) {}

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
}