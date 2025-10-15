import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth';
import { CommonModule } from '@angular/common'; // Importe o CommonModule para usar *ngFor

// NOVO: Define a estrutura de um card de evento
interface Evento {
  id: number;
  titulo: string;
  local: string;
  preco: number;
  imagemUrl: string;
  avaliacao: number;
  totalAvaliacoes: number;
  tag?: string; // Tag opcional como "VIP Access"
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule], // Adicione CommonModule aqui
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {

  // NOVO: ViewChild para pegar a referência do elemento do carrossel no HTML
  @ViewChild('carouselContent') carouselContent!: ElementRef;

  // NOVO: Lista de eventos (mock data)
  public eventos: Evento[] = [
    {
      id: 1,
      titulo: 'Conferência de Tecnologia Avançada',
      local: 'São Paulo, SP',
      preco: 499.90,
      imagemUrl: 'https://images.unsplash.com/photo-1556761175-5973dc0f32e7?w=500',
      avaliacao: 9.8,
      totalAvaliacoes: 1845
    },
    {
      id: 2,
      titulo: 'Workshop de Marketing Digital',
      local: 'Rio de Janeiro, RJ',
      preco: 250.00,
      imagemUrl: 'https://images.unsplash.com/photo-1516321497487-e288fb19713f?w=500',
      avaliacao: 9.5,
      totalAvaliacoes: 972,
      tag: 'ÚLTIMAS VAGAS'
    },
    {
      id: 3,
      titulo: 'Palestra sobre Inovação e Futuro',
      local: 'Curitiba, PR',
      preco: 150.00,
      imagemUrl: 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=500',
      avaliacao: 9.7,
      totalAvaliacoes: 2104
    },
    {
      id: 4,
      titulo: 'Feira de Startups e Negócios',
      local: 'Belo Horizonte, MG',
      preco: 99.90,
      imagemUrl: 'https://images.unsplash.com/photo-1573497491208-6b1acb260507?w=500',
      avaliacao: 9.4,
      totalAvaliacoes: 1530
    },
    {
      id: 5,
      titulo: 'Seminário de Liderança Exponencial',
      local: 'Porto Alegre, RS',
      preco: 750.00,
      imagemUrl: 'https://images.unsplash.com/photo-1552664730-d307ca884978?w=500',
      avaliacao: 9.9,
      totalAvaliacoes: 3012
    }
  ];

  constructor(private router: Router, private authService: AuthService) {}

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // NOVO: Funções para controlar o scroll do carrossel
  scroll(direction: 'left' | 'right') {
    const scrollAmount = 400; // Quantidade de pixels para rolar
    if (direction === 'left') {
      this.carouselContent.nativeElement.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    } else {
      this.carouselContent.nativeElement.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    }
  }
}