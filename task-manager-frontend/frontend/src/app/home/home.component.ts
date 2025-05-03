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
import { statusColors, statusTranslations } from '../../utils/statuses';

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
  userId: number | null = null;
  categories: Category[] = [];
  categoriesForDisplay: Category[] = [];
  tasks: Task[] = [];
  users: User[] = [];

  showAddCategoryPopup: boolean = false;
  showAddTaskPopup: boolean = false;

  isAdminOrModerator: boolean = false;

  ngOnInit() {
    this.username = this.authService.getUsername();
    this.role = this.authService.getRole();
    this.isAdminOrModerator = this.role !== 'USER';
    this.userId = this.authService.getUserId();

    fetchCategories().then((categories) => {
      this.categories = categories;
      this.categoriesForDisplay = categories.slice(0, 3);
    });
    fetchTasks().then((tasks) => {
      this.tasks = tasks.slice(0, 3);
    });
    if (this.isAdminOrModerator)
      fetchUsers().then((users) => (this.users = users));
  }

  postNewCategory(obj: { name: string; userId: number }) {
    postCategory(obj.name, obj.userId).then(() => {
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
    userId: number;
  }) {
    postTask(
      obj.title,
      obj.description,
      obj.status,
      obj.categoryId,
      obj.userId
    ).then((success) => {
      if (success) {
        fetchTasks().then((tasks) => {
          this.tasks = tasks.slice(0, 3);
        });
      }
    });
  }

  getStatusColor(status: string): string {
    return statusColors[status] || '#adb5bd';
  }

  getTranslatedStatus(status: string): string {
    return statusTranslations[status] || status;
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
