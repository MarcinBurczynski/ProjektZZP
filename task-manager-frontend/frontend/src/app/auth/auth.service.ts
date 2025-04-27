import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private cookieService: CookieService) {}

  saveToken(token: string): void {
    this.cookieService.set('jwt_token', token, 1, '/', 'localhost', false, 'Strict');
  }

  getToken(): string | null {
    return this.cookieService.get('jwt_token');
  }

  removeToken(): void {
    this.cookieService.delete('jwt_token', '/');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
