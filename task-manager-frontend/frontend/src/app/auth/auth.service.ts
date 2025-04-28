import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private cookieService: CookieService, private router: Router) {}

  public logout() {
    this.removeToken();
    this.removeRole();
    this.removeUsername();
    this.router.navigate(['/']);
  }

  saveToken(token: string): void {
    this.cookieService.set('jwt_token', token, 1, '/', 'localhost', false, 'Strict');
  }

  getToken(): string | null {
    return this.cookieService.get('jwt_token');
  }

  removeToken(): void {
    this.cookieService.delete('jwt_token', '/');
  }

  saveRole(role: string): void {
    this.cookieService.set('role', role, 1, '/', 'localhost', false, 'Strict');
  }

  getRole(): string | null {
    return this.cookieService.get('role');
  }

  removeRole(): void {
    this.cookieService.delete('role', '/');
  }

  saveUsername(username: string): void {
    this.cookieService.set('username', username, 1, '/', 'localhost', false, 'Strict');
  }

  getUsername(): string | null {
    return this.cookieService.get('username');
  }

  removeUsername(): void {
    this.cookieService.delete('username', '/');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
