import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideRouter, withDebugTracing, withRouterConfig } from '@angular/router';
import { AppComponent } from './app.component';
import { routes } from './app.routes';
import { NgModule } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

@NgModule({
  providers: [CookieService],
})
export class AppModule {}

bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes, withDebugTracing(), withRouterConfig({ paramsInheritanceStrategy: 'always' })),
    provideHttpClient(withFetch())
  ]
})
  .catch(err => console.error(err));
