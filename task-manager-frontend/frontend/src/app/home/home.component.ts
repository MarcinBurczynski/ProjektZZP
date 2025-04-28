import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  constructor(private router: Router, private authService: AuthService) {}

  role: string | null = '';
  username: string | null = '';

  ngOnInit() {
    this.role = this.authService.getRole();
    this.username = this.authService.getUsername();
  }

  logout() {
    this.authService.logout();
  }

  goToCategories() {
    this.router.navigate(['/categories']);
  }

  goToTasks() {
    this.router.navigate(['/tasks']);
  }
}
