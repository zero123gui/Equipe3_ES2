import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin()) {
    return true; // Permite o acesso à rota
  } else {
    router.navigate(['/home']); // Redireciona para a home se não for admin
    return false; // Bloqueia o acesso
  }
};