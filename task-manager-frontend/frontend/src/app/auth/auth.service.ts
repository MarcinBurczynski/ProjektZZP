import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/login'; // Adres backendu

  constructor(private http: HttpClient) {}

  // Funkcja logowania
  login(username: string, password: string): Observable<any> {
    const credentials = { username, password };
    return this.http.post<any>(this.apiUrl, credentials);
  }

  // Zapisz token w localStorage
  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  // Pobierz token z localStorage
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Usuń token (np. przy wylogowywaniu)
  removeToken(): void {
    localStorage.removeItem('token');
  }

  // Sprawdzanie, czy użytkownik jest zalogowany
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
