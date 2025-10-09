import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, of, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  // URL base do seu back-end Java. Verifique se a porta (8080) está correta.
  private backendUrl = 'http://localhost:8080';

  // URL da API externa ViaCEP
  private viaCepUrl = 'https://viacep.com.br/ws';

  constructor(private http: HttpClient) { }

  getEnderecoPorCep(cep: string): Observable<any> {
    return this.http.get(`${this.viaCepUrl}/${cep}/json/`);
  }

  cadastrarParticipante(participante: any): Observable<any> {
    return this.http.post(`${this.backendUrl}/participants`, participante);
  }


  login(credentials: any): Observable<boolean> {
    // Peça ao seu colega do back-end para criar um endpoint POST /login.
    return this.http.post<boolean>(`${this.backendUrl}/login`, credentials).pipe(

      tap(response => console.log('Resposta do login:', response)),
      catchError(err => {
        console.error('Erro no login:', err);
        return of(false); // Retorna um Observable de 'false' em caso de erro.
      })
    );
  }
}