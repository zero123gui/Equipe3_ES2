import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { RegisterSuccess } from './pages/register-success/register-success';
import { Home } from './pages/home/home';
// NOVOS IMPORTS
import { Eventos } from './pages/eventos/eventos';
import { EventoDetalhe } from './pages/evento-detalhe/evento-detalhe';
import { CriarEvento } from './pages/criar-evento/criar-evento';
import { CriarPalestra } from './pages/criar-palestra/criar-palestra'; // <-- IMPORTE AQUI
import { adminGuard } from './guards/admin-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' }, // Mude o padrão para a home

  { path: 'home', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'register-success', component: RegisterSuccess },

  // NOVAS ROTAS
  { path: 'eventos', component: Eventos },
  // Rota de detalhe: o ':id' é um parâmetro dinâmico. 
  // /eventos/1, /eventos/42, etc., vão todos para esta página.
  { path: 'eventos/:id', component: EventoDetalhe }, 
  { path: 'criar-evento', component: CriarEvento },
  { 
    path: 'criar-evento', 
    component: CriarEvento,
    canActivate: [adminGuard] // <-- APLICA A PROTEÇÃO AQUI
  },
  { 
    path: 'criar-palestra', 
    component: CriarPalestra,
    canActivate: [adminGuard]
  },

  { path: '**', redirectTo: '/home' } // Redireciona para a home se a URL não existir
];