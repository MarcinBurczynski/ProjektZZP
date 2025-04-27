import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import apiClient from '../../environments/axios';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class RegisterComponent {
  username: string = '';
  password: string = '';
  confirmPassword: string = '';
  showError: boolean = false;
  errorMessage: string = '';

  constructor(private router: Router) {}

  async register() {
    if (!this.username || !this.password || !this.confirmPassword) {
      this.showError = true;
      this.errorMessage = 'Wszystkie pola są wymagane.';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.showError = true;
      this.errorMessage = 'Hasła się nie zgadzają.';
      return;
    }

    const registrationData = {
      username: this.username,
      password: this.password,
    };

    try {
      const response = await apiClient.post('/register', registrationData);

      if (response.status === 200) {
        console.log('Zarejestrowano poprawnie');
        this.router.navigate(['/']);
      } else {
        this.showError = true;
        this.errorMessage = 'Rejestacja nie powiodła się. Spróbuj ponownie.';
        console.error('Registration failed:', response.status);
      }
    } catch (error) {
      this.showError = true;
      this.errorMessage = 'Rejestacja nie powiodła się. Spróbuj ponownie.';
      console.error('Registration failed:', error);
    }
  }

  navigateToLogin() {
    this.router.navigate(['/']);
  }

  closeModal() {
    this.showError = false;
  }
}
