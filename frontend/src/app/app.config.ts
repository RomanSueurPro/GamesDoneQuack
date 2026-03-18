import { ApplicationConfig, importProvidersFrom, APP_INITIALIZER, inject } from '@angular/core';
import { provideRouter } from '@angular/router';
import { HttpClientModule, HttpClientXsrfModule } from '@angular/common/http';
import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { MAT_DIALOG_DEFAULT_OPTIONS } from '@angular/material/dialog';
import { appInitializer } from './initializers/appInitializer';


export const appConfig: ApplicationConfig = {
  providers: [{provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {hasBackdrop: false}},
    importProvidersFrom(HttpClientModule, 
        HttpClientXsrfModule.withOptions({
          cookieName: 'XSRF-TOKEN',      // default name, matches Spring Security default
          headerName: 'X-XSRF-TOKEN'     // default name Angular expects to send
        })),
    provideRouter(routes),
    provideClientHydration(), provideAnimationsAsync(),
    
    {
      provide: APP_INITIALIZER,
      useFactory: appInitializer,
      multi: true
    }

  ]
  
};