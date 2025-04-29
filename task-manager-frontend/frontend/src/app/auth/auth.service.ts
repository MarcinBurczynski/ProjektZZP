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
    this.removeUserId();
    this.removeUsername();
    this.router.navigate(['/']);
  }

  saveToken(token: string): void {
    this.cookieService.set(
      'taskero_token',
      token,
      1,
      '/',
      'localhost',
      false,
      'Strict'
    );
  }

  getToken(): string | null {
    return this.cookieService.get('taskero_token');
  }

  removeToken(): void {
    this.cookieService.delete('taskero_token', '/');
  }

  saveRole(role: string): void {
    this.cookieService.set(
      'taskero_role',
      role,
      1,
      '/',
      'localhost',
      false,
      'Strict'
    );
  }

  getRole(): string | null {
    return this.cookieService.get('taskero_role');
  }

  removeRole(): void {
    this.cookieService.delete('taskero_role', '/');
  }

  saveUserId(userId: string): void {
    this.cookieService.set(
      'taskero_userId',
      userId,
      1,
      '/',
      'localhost',
      false,
      'Strict'
    );
  }

  getUserId(): string | null {
    return this.cookieService.get('taskero_userId');
  }

  removeUserId(): void {
    this.cookieService.delete('taskero_userId', '/');
  }

  saveUsername(username: string): void {
    this.cookieService.set(
      'taskero_username',
      username,
      1,
      '/',
      'localhost',
      false,
      'Strict'
    );
  }

  getUsername(): string | null {
    return this.cookieService.get('taskero_username');
  }

  removeUsername(): void {
    this.cookieService.delete('taskero_username', '/');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
