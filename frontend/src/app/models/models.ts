// src/app/models/api.models.ts
export interface Palestra {
  id: number;
  nomePalestra: string;
  local: string; 
  descricao: string; 
  qtdVagas: number; 
  dtInicio: string; // Formato "yyyy-MM-dd" [cite: 13, 26, 43, 78]
  dtTermino: string; // Formato "yyyy-MM-dd" [cite: 14, 27, 44, 78]
  horaInicio: string; // Formato "HH:mm:ss" [cite: 15, 28, 45, 79]
  horaTermino: string; // Formato "HH:mm:ss" [cite: 16, 29, 46, 79]
  idEvento: number; 
}

export interface Evento {
  id: number;
  nomeEvento: string; 
  dtInicio: string; 
  dtTermino: string; 
  local: string; 
  descricao: string; 
  urlSite: string; 
  // Adicionaremos um campo opcional para as palestras na página de detalhes
  palestras?: Palestra[];
}

// Interface para a resposta paginada do Spring
export interface Page<T> {
  content: T[];
  pageable: any; 
  totalPages: number; 
  totalElements: number; 
  last: boolean; 
  first: boolean; 
  size: number; 
  number: number; 
  // ...outras propriedades de paginação
}