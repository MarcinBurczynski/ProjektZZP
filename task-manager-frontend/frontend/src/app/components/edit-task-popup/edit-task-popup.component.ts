import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  HostListener,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Category, Task, User } from '../../../utils/interfaces';
import {
  trigger,
  style,
  animate,
  transition,
  query,
} from '@angular/animations';

@Component({
  selector: 'app-edit-task-popup',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-task-popup.component.html',
  styleUrl: './edit-task-popup.component.css',
  standalone: true,
  animations: [
    trigger('popupAnimation', [
      transition(':enter', [
        query(
          '.popup-content',
          [
            style({ opacity: 0, transform: 'translateY(30px)' }),
            animate(
              '250ms ease-out',
              style({ opacity: 1, transform: 'translateY(0)' })
            ),
          ],
          { optional: true }
        ),
      ]),
      transition(':leave', [
        query(
          '.popup-content',
          [
            animate(
              '200ms ease-in',
              style({ opacity: 0, transform: 'translateY(30px)' })
            ),
          ],
          { optional: true }
        ),
      ]),
    ]),
  ],
})
export class EditTaskPopupComponent implements OnChanges {
  @Input() visible: boolean = false;
  @Input() task: Task | null = null;
  @Input() users: User[] = [];
  @Input() categories: Category[] = [];
  @Input() canEditUserId: boolean = false;

  @Output() confirm = new EventEmitter<Task>();
  @Output() cancel = new EventEmitter<void>();

  taskTitle: string = '';
  taskDescription: string = '';
  taskStatus: string = '';
  selectedCategory: string = '';
  selectedUsername: string = '';
  taskCategoryId: number | null = null;
  taskUserId: number | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['task'] && this.task) {
      this.taskTitle = this.task.title;
      this.taskDescription = this.task.description;
      this.taskStatus = this.task.status;
      this.selectedCategory = this.getCategoryNameFromId(this.task.categoryId);
      this.selectedUsername = this.getUsernameFromId(this.task.userId);
      this.taskCategoryId = this.task.categoryId;
      this.taskUserId = this.task.userId;
    }
  }

  onConfirm(): void {
    if (!this.task || this.taskUserId == null || this.taskCategoryId == null)
      return;

    this.confirm.emit({
      ...this.task,
      title: this.taskTitle,
      description: this.taskDescription,
      status: this.taskStatus,
      categoryId: this.taskCategoryId,
      userId: this.taskUserId,
    });

    this.visible = false;
  }

  getUsernameFromId(userId: number): string {
    return this.users.find((user) => user.id === userId)?.username || '';
  }

  getCategoryNameFromId(categoryId: number): string {
    return (
      this.categories.find((category) => category.id === categoryId)?.name || ''
    );
  }

  @HostListener('document:keydown.escape', ['$event'])
  handleEsc() {
    this.close();
  }

  onOverlayClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (target.classList.contains('popup-overlay')) {
      this.close();
    }
  }

  onCancel(): void {
    this.cancel.emit();
    this.visible = false;
  }

  close(callback?: () => void) {
    this.visible = false;
    setTimeout(() => {
      callback?.();
      this.cancel.emit();
    }, 200);
  }
}
