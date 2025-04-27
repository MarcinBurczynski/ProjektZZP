import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideRouter, withDebugTracing, withRouterConfig } from '@angular/router';
import { AppComponent } from './app.component';
import { routes } from './app.routes';


bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes, withDebugTracing(), withRouterConfig({ paramsInheritanceStrategy: 'always' })),
    provideHttpClient(withFetch())
  ]
})
  .catch(err => console.error(err));
