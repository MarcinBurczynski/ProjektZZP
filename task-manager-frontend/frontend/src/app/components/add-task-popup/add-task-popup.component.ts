import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Category } from '../../../utils/interfaces';

@Component({
  selector: 'app-add-task-popup',
  templateUrl: './add-task-popup.component.html',
  styleUrl: './add-task-popup.component.css',
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class AddTaskPopupComponent {
  @Input() visible: boolean = false;
  @Input() categories: Category[] = [];

  @Output() confirm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  taskTitle: string = '';
  taskDescription: string = '';
  taskStatus: string = '';
  taskCategoryId: string = '';

  onConfirm() {
    if (this.taskTitle.trim() && this.taskCategoryId && this.taskStatus) {
      const newTask = {
        title: this.taskTitle.trim(),
        description: this.taskDescription.trim(),
        status: this.taskStatus,
        categoryId: this.taskCategoryId,
      };
      this.confirm.emit(newTask);
      this.resetForm();
    }
  }

  onCancel() {
    this.cancel.emit();
    this.resetForm();
  }

  private resetForm() {
    this.taskTitle = '';
    this.taskDescription = '';
    this.taskStatus = '';
    this.taskCategoryId = '';
  }
}
