import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Api } from '../../services/api';
import { Evento, Palestra } from '../../models/models';

@Component({
  selector: 'app-criar-palestra',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './criar-palestra.html',
  styleUrl: './criar-palestra.css'
})
export class CriarPalestra implements OnInit {

  public palestraForm: FormGroup;
  public eventos: Evento[] = []; // Array para guardar os eventos do dropdown
  public errorMessage = '';
  public successMessage = '';

  constructor(private apiService: Api, private router: Router) {
    this.palestraForm = new FormGroup({
      idEvento: new FormControl('', [Validators.required]),
      nomePalestra: new FormControl('', [Validators.required]),
      local: new FormControl('', [Validators.required]),
      descricao: new FormControl('', [Validators.required]),
      qtdVagas: new FormControl('', [Validators.required, Validators.min(1)]),
      dtInicio: new FormControl('', [Validators.required]),
      dtTermino: new FormControl('', [Validators.required]),
      horaInicio: new FormControl('', [Validators.required]),
      horaTermino: new FormControl('', [Validators.required]),
    });
  }

  ngOnInit(): void {
    this.carregarEventos();
  }

  // Busca os eventos para preencher o <select>
  carregarEventos(): void {
    this.apiService.getEventos(0, 100).subscribe(page => { // Pega até 100 eventos
      this.eventos = page.content;
    });
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.palestraForm.invalid) {
      this.errorMessage = 'Por favor, preencha todos os campos obrigatórios.';
      return;
    }

    const formValues = this.palestraForm.value;

    // Monta o payload para a API, formatando data e hora
    const palestraPayload = {
      nomePalestra: formValues.nomePalestra,
      local: formValues.local,
      descricao: formValues.descricao,
      qtdVagas: parseInt(formValues.qtdVagas, 10),
      dtInicio: formValues.dtInicio,
      dtTermino: formValues.dtTermino,
      // A API espera "HH:mm:ss", mas o input "time" retorna "HH:mm"
      horaInicio: formValues.horaInicio + ':00',
      horaTermino: formValues.horaTermino + ':00',
      idEvento: parseInt(formValues.idEvento, 10)
    };

    this.apiService.createPalestra(palestraPayload as Omit<Palestra, 'id'>).subscribe({
      next: (palestraCriada) => {
        this.successMessage = `Palestra "${palestraCriada.nomePalestra}" criada com sucesso!`;
        this.palestraForm.reset();
        // Opcional: Redireciona para a página do evento
        setTimeout(() => this.router.navigate(['/eventos', palestraCriada.idEvento]), 2000);
      },
      error: (err) => {
        console.error('Erro ao criar palestra:', err);
        this.errorMessage = 'Erro ao criar palestra. Verifique se as datas e horários são válidos.';
      }
    });
  }
}