import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import {
  fetchCategories,
  fetchTasks,
  fetchUsers,
} from '../../utils/fetching_helper';
import { Category, User, Task } from '../../utils/interfaces';
import { AddCategoryPopupComponent } from '../components/add-category-popup/add-category-popup.component';
import { postCategory, postTask } from '../../utils/posting_helper';
import { AddTaskPopupComponent } from '../components/add-task-popup/add-task-popup.component';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  imports: [CommonModule, AddCategoryPopupComponent, AddTaskPopupComponent],
})
export class HomeComponent implements OnInit {
  constructor(private router: Router, private authService: AuthService) {}

  role: string | null = '';
  username: string | null = '';
  categories: Category[] = [];
  categoriesForDisplay: Category[] = [];
  tasks: Task[] = [];
  users: User[] = [];

  showAddCategoryPopup: boolean = false;
  showAddTaskPopup: boolean = false;

  statusColors: { [key: string]: string } = {
    NEW: '#4dabf7',
    IN_PROGRESS: '#ffa94d',
    COMPLETED: '#69db7c',
  };

  ngOnInit() {
    this.role = this.authService.getRole();
    this.username = this.authService.getUsername();
    fetchCategories().then((categories) => {
      this.categories = categories;
      this.categoriesForDisplay = categories.slice(0, 3);
    });
    fetchTasks().then((tasks) => {
      this.tasks = tasks.slice(0, 3);
    });
    fetchUsers().then((users) => {
      this.users = users;
    });
  }

  postNewCategory(name: string) {
    postCategory(name).then(() => {
      fetchCategories().then((categories) => {
        this.categories = categories;
        this.categoriesForDisplay = categories.slice(0, 3);
      });
    });
  }

  postNewTask(obj: {
    title: string;
    description: string;
    status: string;
    categoryId: number;
  }) {
    postTask(
      obj.title,
      obj.description,
      obj.status,
      obj.categoryId,
      Number(this.authService.getUserId())
    ).then(() => {
      fetchTasks().then((tasks) => {
        this.tasks = tasks.slice(0, 3);
      });
    });
  }

  getStatusColor(status: string): string {
    return this.statusColors[status] || '#adb5bd';
  }

  getUsernameFromId(userId: number): string {
    return this.users.find((user) => user.id === userId)?.username || '';
  }

  getCategoryNameFromId(categoryId: number): string {
    return (
      this.categories.find((category) => category.id === categoryId)?.name || ''
    );
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
