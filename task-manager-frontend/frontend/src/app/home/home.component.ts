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
import { postCategory, postTask, postUser } from '../../utils/posting_helper';
import { AddTaskPopupComponent } from '../components/add-task-popup/add-task-popup.component';
import { AddUserPopupComponent } from '../components/add-user-popup/add-user-popup.component';
import { statusColors, statusTranslations } from '../../utils/statuses';
import { roleColors, rolesTranslations } from '../../utils/roles';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
  imports: [
    CommonModule,
    AddCategoryPopupComponent,
    AddTaskPopupComponent,
    AddUserPopupComponent,
  ],
})
export class HomeComponent implements OnInit {
  constructor(private router: Router, private authService: AuthService) {}

  role: string | null = '';
  username: string | null = '';
  userId: number | null = null;
  categories: Category[] = [];
  tasks: Task[] = [];
  users: User[] = [];

  showAddCategoryPopup: boolean = false;
  showAddTaskPopup: boolean = false;
  showAddUserPopup: boolean = false;

  isAdminOrModerator: boolean = false;

  ngOnInit() {
    this.username = this.authService.getUsername();
    this.role = this.authService.getRole();
    this.isAdminOrModerator = this.role !== 'USER';
    this.userId = this.authService.getUserId();

    fetchCategories().then((categories) => {
      this.categories = categories;
    });
    fetchTasks().then((tasks) => {
      this.tasks = tasks;
    });
    if (this.isAdminOrModerator)
      fetchUsers().then((users) => (this.users = users));
  }

  postNewCategory(obj: { name: string; userId: number }) {
    postCategory(obj.name, obj.userId).then(() => {
      fetchCategories().then((categories) => {
        this.categories = categories;
      });
    });
  }

  postNewUser(obj: {
    username: string;
    password: string;
    email: string;
    role: string;
  }) {
    postUser(obj.username, obj.password, obj.email, obj.role).then(() => {
      fetchUsers().then((users) => {
        this.users = users;
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
          this.tasks = tasks;
        });
      }
    });
  }

  getNewTasksNumber(): number {
    return this.tasks.filter(
      (task) => task.userId === this.userId && task.status === 'NEW'
    ).length;
  }

  getRemaningTasksNumber(): number {
    return this.tasks.filter(
      (task) => task.userId === this.userId && task.status === 'IN_PROGRESS'
    ).length;
  }

  getDoneTasksNumber(): number {
    return this.tasks.filter(
      (task) => task.userId === this.userId && task.status === 'COMPLETED'
    ).length;
  }

  getCategoriesNumber(): number {
    return this.categories.filter((category) => category.userId === this.userId)
      .length;
  }

  getNewTasksNumberSystem(): number {
    return this.tasks.filter((task) => task.status === 'NEW').length;
  }

  getRemaningTasksNumberSystem(): number {
    return this.tasks.filter((task) => task.status === 'IN_PROGRESS').length;
  }

  getDoneTasksNumberSystem(): number {
    return this.tasks.filter((task) => task.status === 'COMPLETED').length;
  }

  getCategoriesNumberSystem(): number {
    return this.categories.length;
  }

  getStatusColor(status: string): string {
    return statusColors[status] || '#adb5bd';
  }

  getTranslatedStatus(status: string): string {
    return statusTranslations[status] || status;
  }

  getRoleColor(role: string): string {
    return roleColors[role] || '#adb5bd';
  }

  getTranslatedRole(role: string): string {
    return rolesTranslations[role] || role;
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

  goToUsers() {
    this.router.navigate(['/users']);
  }
}
