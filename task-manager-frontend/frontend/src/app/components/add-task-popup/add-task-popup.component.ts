import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Category, User } from '../../../utils/interfaces';
import {
  trigger,
  style,
  animate,
  transition,
  query,
} from '@angular/animations';

@Component({
  selector: 'app-add-task-popup',
  templateUrl: './add-task-popup.component.html',
  styleUrl: './add-task-popup.component.css',
  standalone: true,
  imports: [CommonModule, FormsModule],
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
export class AddTaskPopupComponent {
  @Input() visible: boolean = false;
  @Input() categories: Category[] = [];
  @Input() users: User[] = [];
  @Input() loggedUserId: number | null = null;
  @Input() canEditUserId: boolean = false;

  @Output() confirm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  taskTitle: string = '';
  taskDescription: string = '';
  taskStatus: string = '';
  taskCategoryId: string = '';
  taskUserId: string = '';

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

  onConfirm() {
    if (this.taskTitle.trim() && this.taskCategoryId && this.taskStatus) {
      const newTask = {
        title: this.taskTitle.trim(),
        description: this.taskDescription.trim(),
        status: this.taskStatus,
        categoryId: this.taskCategoryId,
        userId: this.taskUserId || this.loggedUserId,
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
    this.taskUserId = '';
  }

  close(callback?: () => void) {
    this.visible = false;
    setTimeout(() => {
      callback?.();
      this.cancel.emit();
    }, 200);
  }
}
