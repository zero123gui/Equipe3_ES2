import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, of } from 'rxjs';
import { Router } from '@angular/router';
import { Page } from '../models/models'; // Importa a interface de Página

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private backendUrl = 'http://localhost:8080';

  private _isLoggedIn = new BehaviorSubject<boolean>(this.hasToken());
  isLoggedIn$: Observable<boolean> = this._isLoggedIn.asObservable();

  // --- MUDANÇA: Agora é um BehaviorSubject para ser reativo ---
  // Esta lista guardará os IDs dos eventos em que o usuário está inscrito.
  private registeredEventIds = new BehaviorSubject<number[]>([]);

  constructor(private http: HttpClient, private router: Router) {
    // Se o usuário já tem um token (recarregou a página),
    // busca as inscrições dele.
    if (this.hasToken()) {
      this.loadMyRegistrations();
    }
  }

  private hasToken(): boolean {
    return !!localStorage.getItem('authToken');
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  /**
   * LOGIN: Salva o token e AGORA TAMBÉM BUSCA AS INSCRIÇÕES.
   */
  login(credentials: any): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.backendUrl}/auth/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('authToken', response.token);
        this._isLoggedIn.next(true);

        // --- MUDANÇA: Chama o método para carregar as inscrições ---
        this.loadMyRegistrations();
      })
    );
  }

  /**
   * LOGOUT: Remove o token e LIMPA A LISTA de inscrições.
   */
  logout() {
    localStorage.removeItem('authToken');
    this._isLoggedIn.next(false);
    this.registeredEventIds.next([]); // <-- Limpa a lista de inscrições
    this.router.navigate(['/login']);
  }

  public isAdmin(): boolean {
    return this._isLoggedIn.getValue();
  }

  // --- NOVO MÉTODO: Busca as inscrições reais do usuário ---
  /**
   * Chama o endpoint GET /event-registrations/my e atualiza a lista de IDs.
   */
  loadMyRegistrations(): void {
    if (!this.hasToken()) return; // Não faz nada se não estiver logado

    // Pega "todos" os eventos (até 2000)
    const params = new HttpParams().set('page', '0').set('size', '2000'); 

    this.http.get<Page<any>>(`${this.backendUrl}/event-registrations/my`, { params }).subscribe({
      next: (page) => {
        // *** IMPORTANTE: SUPOSIÇÃO DA ESTRUTURA DA RESPOSTA ***
        // Estou assumindo que a resposta 'EventRegistrationResponseDto'
        // tem um objeto 'evento' dentro dele, que tem um 'id'.
        // Ex: { content: [ { id: 1, evento: { id: 5, nomeEvento: "..." } } ] }
        // Se a estrutura for diferente (ex: só "eventoId: 5"), esta linha precisa ser ajustada.
        const eventIds = page.content.map(registration => registration.evento.id);

        this.registeredEventIds.next(eventIds);
      },
      error: (err) => {
        console.error('Erro ao buscar inscrições:', err);
        // Se der 401 (token expirado), faz logout
        if (err.status === 401) {
          this.logout();
        }
      }
    });
  }

  // --- MÉTODO ATUALIZADO: Agora lê a lista real ---
  /**
   * Verifica (sincronamente) se o usuário está inscrito em um evento.
   */
  isUserRegisteredForEvent(eventId: number): boolean {
    // Lê o valor mais recente da nossa lista de IDs
    return this.registeredEventIds.getValue().includes(eventId);
  }
}