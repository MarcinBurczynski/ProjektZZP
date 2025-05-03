import {
  Component,
  EventEmitter,
  Input,
  Output,
  HostListener,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  trigger,
  style,
  animate,
  transition,
  query,
} from '@angular/animations';

@Component({
  selector: 'app-delete-category-popup',
  imports: [CommonModule],
  templateUrl: './delete-category-popup.component.html',
  styleUrl: './delete-category-popup.component.css',
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
export class DeleteCategoryPopupComponent {
  @Input() visible: boolean = false;
  @Input() itemType: string = 'element';
  @Input() itemName: string = '';
  @Output() confirm = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();

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
    this.close(() => this.confirm.emit());
  }

  close(callback?: () => void) {
    this.visible = false;
    setTimeout(() => {
      callback?.();
      this.cancel.emit();
    }, 200);
  }
}
