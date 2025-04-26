import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

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
      this.errorMessage = 'All fields are required.';
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.showError = true;
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    const registrationData = { username: this.username, password: this.password };

    try {
      const response = await fetch('http://localhost:8080/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(registrationData),
      });

      if (response.ok) {
        console.log('Registered successfully');
        this.router.navigate(['/']);
      } else {
        this.showError = true;
        this.errorMessage = 'Registration failed. Try again.';
        console.error('Registration failed:', response.status);
      }
    } catch (error) {
      this.showError = true;
      this.errorMessage = 'An error occurred. Please try again later.';
      console.error('Error during registration:', error);
    }
  }

  navigateToLogin() {
    this.router.navigate(['/']);
  }

  closeModal() {
    this.showError = false;
  }
}
