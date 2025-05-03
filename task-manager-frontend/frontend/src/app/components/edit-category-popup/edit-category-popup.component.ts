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
import { Category, User } from '../../../utils/interfaces';
import {
  trigger,
  style,
  animate,
  transition,
  query,
} from '@angular/animations';

@Component({
  selector: 'app-edit-category-popup',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-category-popup.component.html',
  standalone: true,
  styleUrl: './edit-category-popup.component.css',
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
export class EditCategoryPopupComponent implements OnChanges {
  @Input() visible: boolean = false;
  @Input() category: Category | null = null;
  @Input() users: User[] = [];
  @Input() canEditUserId: boolean = false;

  @Output() confirm = new EventEmitter<Category>();
  @Output() cancel = new EventEmitter<void>();

  categoryName = '';
  selectedUsername: string = '';
  categoryUserId: number | null = null;

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

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['category'] && this.category) {
      this.categoryName = this.category.name;
      this.categoryUserId = this.category.userId;
      this.selectedUsername = this.getUsernameFromId(this.category.userId);
    }
  }

  getUsernameFromId(userId: number): string {
    const user = this.users.find((user) => user.id === userId);
    return user ? user.username : '';
  }

  onConfirm(): void {
    if (!this.category || this.categoryUserId == null) return;

    const user = this.users.find(
      (user) => user.username === this.selectedUsername
    );

    this.confirm.emit({
      ...this.category,
      name: this.categoryName,
      userId: user ? user.id : this.category.userId,
    });

    this.visible = false;
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
