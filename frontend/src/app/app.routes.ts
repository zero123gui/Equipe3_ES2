import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Register } from './pages/register/register';
import { RegisterSuccess } from './pages/register-success/register-success';
import { Home } from './pages/home/home';

export const routes: Routes = [
  // Se a URL estiver vazia, redireciona para /login
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Mapeamento das telas
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'register-success', component: RegisterSuccess },
  { path: 'home', component: Home },

  // Rota coringa, se o usuário digitar uma URL que não existe
  { path: '**', redirectTo: '/login' },
];
