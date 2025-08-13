import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    importProvidersFrom(HttpClientModule, 
        HttpClientXsrfModule.withOptions({
          cookieName: 'XSRF-TOKEN',      // default name, matches Spring Security default
          headerName: 'X-XSRF-TOKEN'     // default name Angular expects to send
        })),
    provideRouter(routes),
    provideClientHydration(),
  ]
};