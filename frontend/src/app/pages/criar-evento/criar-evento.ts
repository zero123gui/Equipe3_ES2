import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Api } from '../../services/api';
import { Evento } from '../../models/models';

@Component({
  selector: 'app-criar-evento',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './criar-evento.html',
  styleUrl: './criar-evento.css'
})
export class CriarEvento {

  public eventoForm: FormGroup;
  public enderecoVisivel = false;
  public errorMessage = '';
  public successMessage = '';

  constructor(private apiService: Api, private router: Router) {
    // Define a estrutura do formulário e suas validações
    this.eventoForm = new FormGroup({
      nomeEvento: new FormControl('', [Validators.required]),
      dtInicio: new FormControl('', [Validators.required]),
      dtTermino: new FormControl('', [Validators.required]),
      descricao: new FormControl('', [Validators.required]),
      urlSite: new FormControl(''), // Opcional
      // Endereço
      cep: new FormControl('', [Validators.required]),
      logradouro: new FormControl(''),
      numero: new FormControl('', [Validators.required]),
      complemento: new FormControl(''),
      bairro: new FormControl(''),
      localidade: new FormControl(''),
      uf: new FormControl('')
    });
  }

  buscarCep() {
    const cep = this.eventoForm.get('cep')?.value.replace(/\D/g, '');
    if (cep && cep.length === 8) {
      this.apiService.getEnderecoPorCep(cep).subscribe((data: any) => {
        if (!data.erro) {
          // Preenche os campos do formulário com os dados do ViaCEP
          this.eventoForm.patchValue({
            logradouro: data.logradouro,
            bairro: data.bairro,
            localidade: data.localidade,
            uf: data.uf
          });
          this.enderecoVisivel = true;
        } else {
          alert('CEP não encontrado!');
          this.enderecoVisivel = false;
        }
      });
    }
  }

  formatarCep(event: any) {
  let cep = event.target.value.replace(/\D/g, ''); // Remove tudo que não é dígito

  if (cep.length > 5) {
    // Adiciona o hífen após o 5º dígito
    cep = cep.substring(0, 5) + '-' + cep.substring(5, 8);
  }

  // Atualiza o valor no formulário reativo
  this.eventoForm.get('cep')?.setValue(cep, { emitEvent: false });

  // Se o CEP formatado atingir o tamanho completo, chama a busca
  if (cep.length === 9) {
    this.buscarCep();
  }
  }

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.eventoForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      return;
    }

    const formValues = this.eventoForm.value;

    // Monta o objeto que será enviado para a API
    const eventoPayload: Omit<Evento, 'id'> = {
      nomeEvento: formValues.nomeEvento,
      dtInicio: formValues.dtInicio,
      dtTermino: formValues.dtTermino,
      // Concatena as partes do endereço em um único campo 'local'
      local: `${formValues.logradouro}, ${formValues.numero} - ${formValues.bairro}, ${formValues.localidade} - ${formValues.uf}`,
      descricao: formValues.descricao,
      urlSite: formValues.urlSite
    };

    this.apiService.createEvento(eventoPayload).subscribe({
      next: (eventoCriado) => {
        this.successMessage = `Evento "${eventoCriado.nomeEvento}" criado com sucesso!`;
        this.eventoForm.reset();
        // Opcional: redirecionar após um tempo
        setTimeout(() => this.router.navigate(['/eventos', eventoCriado.id]), 2000);
      },
      error: (err) => {
        console.error('Erro ao criar evento:', err);
        this.errorMessage = 'Ocorreu um erro ao criar o evento. Verifique os dados e tente novamente.';
      }
    });
  }
}