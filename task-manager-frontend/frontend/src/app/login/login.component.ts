import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';
import apiClient from '../../environments/axios';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
  animations: [
    trigger('fadeInOut', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate('300ms', style({ opacity: 1 }))
      ]),
      transition(':leave', [
        animate('300ms', style({ opacity: 0 }))
      ])
    ])
  ]
})
export class LoginComponent {
  isRegistering: boolean = false;
  loginUsername: string = '';
  loginPassword: string = '';
  registerUsername: string = '';
  registerPassword: string = '';
  registerPasswordConfirmation: string = '';
  showError: boolean = false;
  errorMessage: string = '';

  constructor(private router: Router, private authService: AuthService) {}

  async login() {
    if(!this.loginUsername || !this.loginPassword) {
      this.showError = true;
      this.errorMessage = 'Wszystkie pola są wymagane.';
      return;
    }

    try {
        const response = await apiClient.post('/login', {
          username: this.loginUsername,
          password: this.loginPassword,
        });
        if(response.status === 200) {
            const token = await response.data.jwt;
            const role = await response.data.role;
            this.authService.saveToken(token);
            this.authService.saveRole(role);
            this.authService.saveUsername(this.loginUsername);
            this.router.navigate(['/home']);
        } else {
            this.showError = true;
            this.errorMessage = 'Niepoprawna nazwa użytkownika lub hasło. Spróbuj ponownie.'
        }
    } catch (error) {
      this.showError = true;
      this.errorMessage = 'Wystąpił problem podczas próby logowania. Proszę spróbuj ponownie.';
    }
  }

  async register() {
    if(!this.registerUsername || !this.registerPassword || !this.registerPasswordConfirmation) {
        this.showError = true;
        this.errorMessage = 'Wszystkie pola są wymagane.';
        return;
    }

    try {
        const response = await apiClient.post('/register', {
            username: this.registerUsername,
            password: this.registerPassword,
        });
        if(response.status === 200) {
            this.router.navigate(['/']);
        }
        if (response.status === 201) {
            this.isRegistering = false;
            this.loginUsername = this.registerUsername;
            this.loginPassword = '';
            this.registerUsername = '';
            this.registerPassword = '';
        }
    } catch (error) {
        this.showError = true;
        this.errorMessage = 'Nie udało się zarejestrować. Spróbuj ponownie.';
    }
  }

  closeModal() {
    this.showError = false;
  }
}
