import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Api } from '../../services/api'; // Use 'Api' como nos seus outros arquivos
import { Palestra } from '../../models/models'; // Use 'models' como nos seus outros arquivos

@Component({
  selector: 'app-palestra-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './palestra-form.html',
  styleUrl: './palestra-form.css'
})
export class PalestraForm {
  @Input() idEvento!: number; // Recebe o ID do evento pai
  @Output() palestraCriada = new EventEmitter<void>(); // Avisa que a palestra foi criada
  @Output() cancel = new EventEmitter<void>(); // Avisa para fechar o form

  public palestraForm: FormGroup;
  public errorMessage = '';

  constructor(private apiService: Api) {
    this.palestraForm = new FormGroup({
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

  onSubmit(): void {
    this.errorMessage = '';
    if (this.palestraForm.invalid) {
      this.errorMessage = 'Todos os campos são obrigatórios.';
      return;
    }

    const formValues = this.palestraForm.value;

    // Monta o payload exatamente como a API espera [cite: 144-154]
    const palestraPayload = {
      ...formValues,
      qtdVagas: parseInt(formValues.qtdVagas, 10),
      horaInicio: formValues.horaInicio + ':00', // Formata para HH:mm:ss [cite: 151, 215]
      horaTermino: formValues.horaTermino + ':00', // Formata para HH:mm:ss [cite: 152, 215]
      idEvento: this.idEvento // Pega o ID do evento pai
    };

    this.apiService.createPalestra(palestraPayload as Omit<Palestra, 'id'>).subscribe({
      next: () => {
        alert('Palestra criada com sucesso!');
        this.palestraForm.reset();
        this.palestraCriada.emit(); // Avisa o modal pai para recarregar a lista
      },
      error: (err: any) => {
        console.error('Erro ao criar palestra:', err);
        this.errorMessage = 'Erro ao criar palestra. Verifique se as datas e horários são válidos.';
      }
    });
  }
}