import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs'; // Removido switchMap que não é mais necessário aqui
import { Evento, Palestra, Page } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class Api {

  private backendUrl = 'http://localhost:8080';
  private viaCepUrl = 'https://viacep.com.br/ws';

  constructor(private http: HttpClient) { }

  // --- Métodos de Autenticação/Cadastro ---

  /**
   * NOVO: Cadastro completo de participante (com endereço e login)
   * Corresponde a: POST /auth/register-full
   */
  registerFullParticipante(dadosCadastro: any): Observable<any> {
    // Prepara o payload conforme a nova API
    const payload = {
      idTipoParticipante: 1, // Mantendo fixo por enquanto
      nome: dadosCadastro.nome,
      email: dadosCadastro.email,
      telefone: dadosCadastro.telefone, // Garanta que 'telefone' existe no seu objeto participante
      senha: dadosCadastro.senha,
      endereco: {
        cep: dadosCadastro.endereco.cep.replace(/\D/g, ''), // Envia CEP sem hífen
        logradouro: dadosCadastro.endereco.logradouro,
        bairro: dadosCadastro.endereco.bairro,
        localidade: dadosCadastro.endereco.localidade,
        uf: dadosCadastro.endereco.uf,
        numero: dadosCadastro.endereco.numero,
        complemento: dadosCadastro.endereco.complemento
      }
    };
    return this.http.post(`${this.backendUrl}/auth/register-full`, payload);
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
   * NOVO: Cria um novo evento com endereço completo (ADMIN)
   * Corresponde a: POST /events/full
   */
  createEventoFull(dadosEvento: any): Observable<any> {
    // Prepara o payload com o objeto 'endereco' aninhado
    const payload = {
      nomeEvento: dadosEvento.nomeEvento,
      dtInicio: dadosEvento.dtInicio,
      dtTermino: dadosEvento.dtTermino,
      descricao: dadosEvento.descricao,
      urlSite: dadosEvento.urlSite,
      endereco: {
        cep: dadosEvento.cep.replace(/\D/g, ''), // Envia CEP sem hífen
        logradouro: dadosEvento.logradouro,
        bairro: dadosEvento.bairro,
        localidade: dadosEvento.localidade,
        uf: dadosEvento.uf,
        numero: dadosEvento.numero,
        complemento: dadosEvento.complemento
      }
    };
    return this.http.post<{ idEvento: number, idEndereco: number }>(`${this.backendUrl}/events/full`, payload);
  }

  // --- Métodos de Palestra ---
  // (Métodos getPalestras, getPalestrasByEventoId e createPalestra continuam os mesmos)

   /**
   * Busca um endereço a partir de um CEP usando a API ViaCEP.
   */
   getEnderecoPorCep(cep: string): Observable<any> {
    // Garante que o CEP enviado para ViaCEP esteja limpo
    const cepLimpo = cep.replace(/\D/g, '');
    return this.http.get(`${this.viaCepUrl}/${cepLimpo}/json/`);
  }

  /**
   * Cria uma nova palestra (ADMIN)
   * POST /talks
   */
  createPalestra(palestra: Omit<Palestra, 'id'>): Observable<Palestra> {
    return this.http.post<Palestra>(`${this.backendUrl}/talks`, palestra);
  }

  /**
   * Lista todas as palestras (público)
   * GET /talks
   */
  getPalestras(page: number = 0, size: number = 10): Observable<Page<Palestra>> {
      const params = new HttpParams()
          .set('page', page.toString())
          .set('size', size.toString());
      return this.http.get<Page<Palestra>>(`${this.backendUrl}/talks`, { params });
  }

  /**
   * Busca palestras de um evento específico (lógica no front)
   * GET /talks e filtra
   */
  getPalestrasByEventoId(idEvento: number): Observable<Palestra[]> {
      return this.http.get<Page<Palestra>>(`${this.backendUrl}/talks?size=2000`).pipe(
          switchMap(page => {
              const palestrasFiltradas = page.content.filter((p: Palestra) => p.idEvento === idEvento);
              return new Observable<Palestra[]>(observer => observer.next(palestrasFiltradas));
          })
      );
  }
}