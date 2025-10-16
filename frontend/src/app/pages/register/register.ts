import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../../services/api';

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
    endereco: {
      cep: '',
      logradouro: '',
      bairro: '',
      localidade: '',
      uf: '',
      numero: '', // <-- NOVO: Campo para o número
      complemento: '' // <-- NOVO: Campo para o complemento
    }
  };

  public confirmaSenha = '';
  public errorMessage = '';

  // NOVO: Variável para controlar a visibilidade dos campos de endereço
  public enderecoVisivel = false;

  constructor(private router: Router, private api: Api) {}

  formatarCep(event: any) {
  let cep = event.target.value.replace(/\D/g, ''); // Remove tudo que não é dígito

  if (cep.length > 5) {
    // Adiciona o hífen após o 5º dígito
    cep = cep.substring(0, 5) + '-' + cep.substring(5, 8);
  }

  // Atualiza o valor no nosso objeto
  this.participante.endereco.cep = cep;

  if (cep.length === 9) {
    this.buscarCep();
  }
}

  buscarCep() {
  // Limpa o CEP, removendo o hífen e qualquer outro caractere não numérico
  const cepLimpo = this.participante.endereco.cep.replace(/\D/g, '');

  if (cepLimpo && cepLimpo.length === 8) {
    this.api.getEnderecoPorCep(cepLimpo).subscribe((data: any) => {
      if (!data.erro) {
        this.participante.endereco.logradouro = data.logradouro;
        this.participante.endereco.bairro = data.bairro;
        this.participante.endereco.localidade = data.localidade;
        this.participante.endereco.uf = data.uf;
        this.enderecoVisivel = true; 
      } else {
        alert('CEP não encontrado!');
        this.enderecoVisivel = false; 
      }
    });
  }
}

  createAccount() {
  this.errorMessage = '';

  if (this.participante.senha !== this.confirmaSenha) {
    this.errorMessage = 'As senhas não conferem!';
    return;
  }

  // MUDANÇA: Chama o novo método de fluxo completo no ApiService
  this.api.cadastrarFluxoCompleto(this.participante).subscribe({
    next: (response: any) => {
      // Sucesso!
      console.log('Login criado:', response);
      alert('Cadastro realizado com sucesso! Agora você pode fazer o login.');
      this.router.navigate(['/login']); // Redireciona para o login
    },
    error: (err: any) => {
      // Tratamento de erro
      if (err.status === 409) {
        this.errorMessage = 'Este e-mail ou participante já possui um cadastro.';
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