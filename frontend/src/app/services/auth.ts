import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private backendUrl = 'http://localhost:8080'; // Verifique se a URL e a porta estão corretas

  // BehaviorSubject para emitir o status de login para toda a aplicação
  private _isLoggedIn = new BehaviorSubject<boolean>(this.hasToken());
  isLoggedIn$: Observable<boolean> = this._isLoggedIn.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  // Verifica se o token existe no localStorage
  private hasToken(): boolean {
    return !!localStorage.getItem('authToken');
  }

  // Pega o token do localStorage
  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  /**
   * LOGIN: Envia credenciais e, se bem-sucedido, salva o token.
   * Corresponde a: POST /auth/login
   */
  login(credentials: any): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.backendUrl}/auth/login`, credentials).pipe(
      tap(response => {
        // Salva o token no localStorage
        localStorage.setItem('authToken', response.token);
        // Emite o novo status de login (true)
        this._isLoggedIn.next(true);
      })
    );
  }

  /**
   * LOGOUT: Remove o token e atualiza o status de login.
   */
  logout() {
    localStorage.removeItem('authToken');
    this._isLoggedIn.next(false);
    this.router.navigate(['/login']); // Redireciona para o login por segurança
  }

    public isAdmin(): boolean {
    // Por enquanto, vamos simular que todo usuário logado é admin.
    // Em um app real, você decodificaria o token JWT para verificar as "roles".
    return this._isLoggedIn.getValue();
  }
}