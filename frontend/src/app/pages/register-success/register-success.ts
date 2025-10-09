import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register-success',
  standalone: true,
  imports: [],
  templateUrl: './register-success.html',
  styleUrl: './register-success.css',
})
export class RegisterSuccess implements OnInit {
  constructor(private router: Router) {}

  ngOnInit(): void {
    // Espera 3 segundos e depois navega para a tela de login
    setTimeout(() => {
      this.router.navigate(['/login']);
    }, 3000); // 3000 milissegundos = 3 segundos
  }
}
