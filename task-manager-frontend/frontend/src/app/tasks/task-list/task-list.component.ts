import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { AddTaskPopupComponent } from '../../components/add-task-popup/add-task-popup.component';
import { Category, Task, User } from '../../../utils/interfaces';
import {
  fetchCategories,
  fetchTasks,
  fetchUsers,
} from '../../../utils/fetching_helper';
import { statusColors, statusTranslations } from '../../../utils/statuses';
import { postTask } from '../../../utils/posting_helper';
import { DeleteElementPopupComponent } from '../../components/delete-element-popup/delete-element-popup.component';
import { deleteTask } from '../../../utils/deleting_helper';
import { EditTaskPopupComponent } from '../../components/edit-task-popup/edit-task-popup.component';
import { putTask } from '../../../utils/putting_helper';

@Component({
  selector: 'app-task-list',
  imports: [
    CommonModule,
    AddTaskPopupComponent,
    DeleteElementPopupComponent,
    EditTaskPopupComponent,
  ],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css',
})
export class TaskListComponent implements OnInit {
  constructor(private router: Router, private authService: AuthService) {}

  tasks: Task[] = [];
  users: User[] = [];
  categories: Category[] = [];
  username: string | null = '';
  userId: number | null = null;
  role: string | null = '';

  showAddTaskPopup: boolean = false;
  showDeletePopup: boolean = false;
  showEditTaskPopup: boolean = false;

  taskToDelete: Task | null = null;
  taskToEdit: Task | null = null;

  isAdminOrModerator: boolean = false;

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.role = this.authService.getRole();
    this.userId = this.authService.getUserId();
    this.isAdminOrModerator = this.role !== 'USER';
    fetchTasks().then((tasks) => (this.tasks = tasks));
    fetchCategories().then((categories) => (this.categories = categories));
    if (this.isAdminOrModerator)
      fetchUsers().then((users) => (this.users = users));
  }

  editTask(task: Task): void {
    this.taskToEdit = { ...task };
    this.showEditTaskPopup = true;
  }

  handleConfirmEdit(updatedTask: Task): void {
    ``;
    putTask(
      updatedTask.id,
      updatedTask.title,
      updatedTask.description,
      updatedTask.status,
      updatedTask.categoryId,
      updatedTask.userId
    ).then((success) => {
      if (success) {
        const index = this.tasks.findIndex((t) => t.id === updatedTask.id);
        if (index !== -1) {
          this.tasks[index] = updatedTask;
        }
      }
    });
    this.showEditTaskPopup = false;
    this.taskToEdit = null;
  }

  handleCancelEdit(): void {
    this.showEditTaskPopup = false;
    this.taskToEdit = null;
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

  deleteTask(task: Task): void {
    this.taskToDelete = task;
    this.showDeletePopup = true;
  }

  handleConfirmDelete(): void {
    if (!this.taskToDelete) return;
    const toDeleteId: number = this.taskToDelete.id;
    deleteTask(this.taskToDelete.id).then((success) => {
      if (success) {
        const index = this.tasks.findIndex((c) => c.id === toDeleteId);
        if (index !== -1) {
          this.tasks.splice(index, 1);
        }
      }
    });
    this.showDeletePopup = false;
    this.taskToDelete = null;
  }

  handleCancelDelete(): void {
    this.showDeletePopup = false;
    this.taskToDelete = null;
  }

  getUsernameFromId(userId: number): string {
    return this.users.find((user) => user.id === userId)?.username || '';
  }

  getCategoryNameFromId(categoryId: number): string {
    return (
      this.categories.find((category) => category.id === categoryId)?.name || ''
    );
  }

  getStatusColor(status: string): string {
    return statusColors[status] || '#adb5bd';
  }

  getTranslatedStatus(status: string): string {
    return statusTranslations[status] || status;
  }

  logout() {
    this.authService.logout();
  }

  goToHome() {
    this.router.navigate(['/home']);
  }
}
