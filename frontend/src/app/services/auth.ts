import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, of } from 'rxjs';
import { Router } from '@angular/router';
// CORREÇÃO: O caminho correto para o arquivo de modelos
import { Page } from '../models/models'; 

// Interface local (isto está OK)
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

  private registeredEventIds = new BehaviorSubject<number[]>([]);
  public registeredEventIds$ = this.registeredEventIds.asObservable();

  // O construtor vazio está CORRETO para corrigir o loop
  constructor(private http: HttpClient, private router: Router) {
  }

  // O loadMyRegistrations está CORRETO
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

  // O hasToken está CORRETO
  public hasToken(): boolean {
    return !!localStorage.getItem('authToken');
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  // O login está CORRETO
  login(credentials: any): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.backendUrl}/auth/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('authToken', response.token);
        this._isLoggedIn.next(true);
        this.loadMyRegistrations();
      })
    );
  }

  // O logout está CORRETO
  logout() {
    localStorage.removeItem('authToken');
    this._isLoggedIn.next(false);
    this.registeredEventIds.next([]);
    this.router.navigate(['/login']);
  }

  // O isAdmin está CORRETO
  public isAdmin(): boolean {
    return this._isLoggedIn.getValue();
  }

  // O isUserRegisteredForEvent está CORRETO
  isUserRegisteredForEvent(eventId: number): boolean {
    return this.registeredEventIds.getValue().includes(eventId);
  }

  // O registerForEvent está CORRETO
  registerForEvent(eventoId: number): Observable<any> {
    const body = {
      idEvento: eventoId,
      idTipoInscricao: 1
    };
    return this.http.post(`${this.backendUrl}/event-registrations`, body).pipe(
      tap(() => {
        this.loadMyRegistrations();
      })
    );
  }
}