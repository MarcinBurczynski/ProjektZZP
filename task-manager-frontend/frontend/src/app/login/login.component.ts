import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import apiClient from '../../environments/axios';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  showError: boolean = false;
  errorMessage: string = '';

  constructor(private router: Router) {}

  async login() {
    if (this.username && this.password) {
      try {
        const response = await apiClient.post('/login', {
          username: this.username,
          password: this.password,
        });

        if (response.status === 200) {
          const token = await response.data.token;
          localStorage.setItem('authToken', token);
          this.router.navigate(['/home']);
        } else {
          this.showError = true;
          this.errorMessage =
            'Niepoprawna nazwa użytkownika lub hasło. Spróbuj ponownie.';
          console.error('Login failed:', response.status);
        }
      } catch (error) {
        this.showError = true;
        this.errorMessage =
          'Wystąpił problem podczas próby logowania. Proszę spróbuj ponownie.';
        console.error('Error occurred during login:', error);
      }
    } else {
      this.showError = true;
      this.errorMessage = 'Oba pola są wymagane.';
    }
  }

  closeModal() {
    this.showError = false;
  }
  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
