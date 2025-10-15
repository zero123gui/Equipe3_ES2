import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

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
}