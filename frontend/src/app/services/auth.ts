import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, of } from 'rxjs';
import { Router } from '@angular/router';
import { Page } from '../models/models'; // Importa a interface de Página

interface EventRegistrationDto {
  id: number;
  idEvento: number;
  idTipoInscricao: number;
  idParticipante: number;
}

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

  public registeredEventIds$ = this.registeredEventIds.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    // Se o usuário já tem um token (recarregou a página),
    // busca as inscrições dele.
    if (this.hasToken()) {
      this.loadMyRegistrations();
    }
  }

  loadMyRegistrations(): void {
      if (!this.hasToken()) return;
      const params = new HttpParams().set('page', '0').set('size', '2000'); 
      this.http.get<Page<EventRegistrationDto>>(`${this.backendUrl}/event-registrations/my`, { params }).subscribe({
          next: (page) => {
              const eventIds = page.content.map(registration => registration.idEvento);
              this.registeredEventIds.next(eventIds);
          },
          error: (err: any) => {
              console.error('Erro ao buscar inscrições:', err);
              if (err.status === 401) { this.logout(); }
          }
      });
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


  // --- MÉTODO ATUALIZADO: Agora lê a lista real ---
  /**
   * Verifica (sincronamente) se o usuário está inscrito em um evento.
   */
  isUserRegisteredForEvent(eventId: number): boolean {
    return this.registeredEventIds.getValue().includes(eventId);
  }

 registerForEvent(eventoId: number): Observable<any> {
  // O body que você usou no seu teste do PowerShell
  const body = { 
    idEvento: eventoId, 
    idTipoInscricao: 1 // 1 = Ouvinte (como no seu teste)
  };

  // Chama o endpoint real de inscrição
  return this.http.post(`${this.backendUrl}/event-registrations`, body).pipe(
    tap(() => {
      // --- A MÁGICA ESTÁ AQUI ---
      // Após o POST ser bem-sucedido, ele manda o AuthService
      // recarregar a lista de inscrições.
      console.log('Inscrição realizada, recarregando lista de eventos inscritos...');
      this.loadMyRegistrations();
    })
  );
}
}