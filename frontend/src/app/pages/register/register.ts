import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api'; // <-- IMPORTE O SERVIÇO

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {

  // Objeto para vincular os dados do formulário
  public participante = {
    nome: '',
    email: '',
    telefone: '',
    senha: '',
    // Objeto para o endereço
    endereco: {
      cep: '',
      logradouro: '',
      complemento: '',
      bairro: '',
      localidade: '',
      uf: ''
    }
  };

  public confirmaSenha = '';
  public errorMessage = ''; // Para exibir mensagens de erro

  // Injeta o Router e nosso ApiService
  constructor(private router: Router, private apiService: ApiService) {}

  // Função chamada quando o campo CEP perde o foco
  buscarCep() {
    if (this.participante.endereco.cep && this.participante.endereco.cep.length === 8) {
      this.apiService.getEnderecoPorCep(this.participante.endereco.cep).subscribe(data => {
        if (!data.erro) {
          // Preenche os campos de endereço com a resposta da API ViaCEP
          this.participante.endereco.logradouro = data.logradouro;
          this.participante.endereco.bairro = data.bairro;
          this.participante.endereco.localidade = data.localidade;
          this.participante.endereco.uf = data.uf;
        } else {
          alert('CEP não encontrado!');
        }
      });
    }
  }

  createAccount() {
    this.errorMessage = ''; // Limpa a mensagem de erro anterior

    if (this.participante.senha !== this.confirmaSenha) {
      this.errorMessage = 'As senhas não conferem!';
      return;
    }

    // Chama o método de cadastro do nosso serviço
    this.apiService.cadastrarParticipante(this.participante).subscribe({
      next: (response) => {
        // Sucesso! Navega para a tela de sucesso.
        console.log('Participante cadastrado:', response);
        this.router.navigate(['/register-success']);
      },
      error: (err) => {
        // Tratamento de erro
        if (err.status === 400 || err.status === 409) { // 409 Conflict é comum para emails duplicados
          this.errorMessage = 'Este e-mail já está cadastrado. Tente outro.';
        } else {
          this.errorMessage = 'Ocorreu um erro ao tentar cadastrar. Tente novamente.';
        }
        console.error('Erro no cadastro:', err);
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}