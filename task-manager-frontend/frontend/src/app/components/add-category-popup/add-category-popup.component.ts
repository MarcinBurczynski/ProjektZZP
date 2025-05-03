import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  trigger,
  style,
  animate,
  transition,
  query,
} from '@angular/animations';
import { User } from '../../../utils/interfaces';

@Component({
  selector: 'app-add-category-popup',
  templateUrl: './add-category-popup.component.html',
  styleUrls: ['./add-category-popup.component.css'],
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
export class AddCategoryPopupComponent {
  @Input() visible: boolean = false;
  @Input() users: User[] = [];
  @Input() loggedUserId: number | null = null;
  @Input() canEditUserId: boolean = false;
  @Output() confirm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  categoryName: string = '';
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

  onConfirm() {
    if (this.categoryName.trim()) {
      this.confirm.emit({
        name: this.categoryName.trim(),
        userId: this.categoryUserId || this.loggedUserId,
      });
      this.categoryName = '';
      this.categoryUserId = null;
    }
  }

  onCancel() {
    this.cancel.emit();
    this.categoryName = '';
    this.categoryUserId = null;
  }

  close(callback?: () => void) {
    this.visible = false;
    setTimeout(() => {
      callback?.();
      this.cancel.emit();
    }, 200);
  }
}
