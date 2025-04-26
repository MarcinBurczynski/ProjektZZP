import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router'; // <-- Dodaj import Routera

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

  constructor(private router: Router) {} // <-- Inject router w konstruktorze

  async login() {
    if (this.username && this.password) {
      const loginData = { username: this.username, password: this.password };

      try {
        const response = await fetch('http://localhost:8080/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(loginData),
        });

        if (response.ok) {
          const token = await response.text();
          console.log('Logged in successfully, token:', token);
          localStorage.setItem('authToken', token);

          this.router.navigate(['/home']);
        } else {
          this.showError = true;
          this.errorMessage = 'Invalid username or password. Please try again.';
          console.error('Login failed:', response.status);
        }
      } catch (error) {
        this.showError = true;
        this.errorMessage = 'An error occurred during login. Please try again later.';
        console.error('Error occurred during login:', error);
      }
    } else {
      this.showError = true;
      this.errorMessage = 'Both fields are required.';
    }
  }

  closeModal() {
    this.showError = false;
  }
  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
