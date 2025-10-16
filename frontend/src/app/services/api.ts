import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs';
import { Evento, Palestra, Page } from '../models/models'; // Importa nossas novas interfaces


@Injectable({
  providedIn: 'root'
})
export class Api {

  private backendUrl = 'http://localhost:8080';
  private viaCepUrl = 'https://viacep.com.br/ws';

  constructor(private http: HttpClient) { }

  /**
   * CADASTRO (FLUXO COMPLETO):
   * 1. Cria o participante (POST /participants)
   * 2. Usa o ID retornado para criar o login (POST /auth/register)
   */
  cadastrarFluxoCompleto(dadosCadastro: any): Observable<any> {
    // PREPARA O CORPO PARA CRIAR O PARTICIPANTE
    // NOTA: idTipoParticipante e idEndereco estão fixos por enquanto.
    // O ideal é que eles venham de uma seleção no formulário.
    const participantePayload = {
      nome: dadosCadastro.nome,
      idTipoParticipante: 1, // Fixo por enquanto
      idEndereco: 1, // Fixo por enquanto
      complementoEndereco: dadosCadastro.endereco.complemento,
      nroEndereco: dadosCadastro.endereco.numero
    };

    // PASSO 1: Criar o participante
    return this.http.post<any>(`${this.backendUrl}/participants`, participantePayload).pipe(
      switchMap(participanteCriado => {
        // PASSO 2: Usar o ID retornado para criar o login
        const loginPayload = {
          participanteId: participanteCriado.id,
          email: dadosCadastro.email,
          senha: dadosCadastro.senha
        };
        return this.http.post(`${this.backendUrl}/auth/register`, loginPayload);
      })
    );
  }

  /**
   * Busca um endereço a partir de um CEP usando a API ViaCEP.
   */
  getEnderecoPorCep(cep: string): Observable<any> {
    return this.http.get(`${this.viaCepUrl}/${cep}/json/`);
  }

  // --- Métodos de Evento ---

  /**
   * Lista eventos de forma paginada (público)
   * GET /events
   */
  getEventos(page: number = 0, size: number = 10): Observable<Page<Evento>> { 
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Evento>>(`${this.backendUrl}/events`, { params }); 
  }

  /**
   * Busca um evento específico por ID (público)
   * GET /events/{id}
   */
  getEventoById(id: number): Observable<Evento> { 
    return this.http.get<Evento>(`${this.backendUrl}/events/${id}`); 
  }

  /**
   * Cria um novo evento (ADMIN)
   * POST /events
   */
  createEvento(evento: Omit<Evento, 'id'>): Observable<Evento> { 
    return this.http.post<Evento>(`${this.backendUrl}/events`, evento); 
  }

  // --- Métodos de Palestra ---

  /**
   * Lista todas as palestras (público) - Pode ser útil para uma página geral de palestras
   * GET /talks
   */
  getPalestras(page: number = 0, size: number = 10): Observable<Page<Palestra>> { 
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Palestra>>(`${this.backendUrl}/talks`, { params });
  }

  createPalestra(palestra: Omit<Palestra, 'id'>): Observable<Palestra> {
    return this.http.post<Palestra>(`${this.backendUrl}/talks`, palestra);
  }

  /**
   * Busca palestras de um evento específico (lógica a ser implementada no front)
   * Esta função filtra as palestras pelo idEvento.
   */
  getPalestrasByEventoId(idEvento: number): Observable<Palestra[]> {
    // O back-end não forneceu um endpoint /events/{id}/talks.
    // Então, a estratégia é buscar todas as palestras e filtrar no front-end.
    // Se o número de palestras for muito grande, o ideal seria pedir um endpoint novo ao back-end.
    return this.http.get<Page<Palestra>>(`${this.backendUrl}/talks?size=2000`).pipe( // Pega um número grande para simular "todos"
        switchMap(page => {
            const palestrasFiltradas = page.content.filter((p: Palestra) => p.idEvento === idEvento);
            return new Observable<Palestra[]>(observer => observer.next(palestrasFiltradas));
        })
    );
  }
}