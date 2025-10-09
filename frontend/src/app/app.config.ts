import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { FormsModule } from '@angular/forms'; // <-- IMPORTE AQUI
import { provideHttpClient } from '@angular/common/http'; // <-- E AQUI

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(), // <-- ADICIONE AQUI
    importProvidersFrom(FormsModule), // <-- E AQUI
  ],
};
