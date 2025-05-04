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

@Component({
  selector: 'app-add-user-popup',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-user-popup.component.html',
  styleUrl: './add-user-popup.component.css',
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
export class AddUserPopupComponent {
  @Input() visible: boolean = false;

  @Output() confirm = new EventEmitter<any>();
  @Output() cancel = new EventEmitter<void>();

  username: string = '';
  password: string = '';
  repeatPassword: string = '';
  email: string = '';
  role: string = '';

  onConfirm() {
    if (
      this.username.trim() &&
      this.password.trim() &&
      this.email.trim() &&
      this.role.trim()
    ) {
      const newUser = {
        username: this.username.trim(),
        password: this.password.trim(),
        email: this.email.trim(),
        role: this.role.trim(),
      };
      this.confirm.emit(newUser);
      this.resetForm();
    }
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

  onCancel() {
    this.cancel.emit();
    this.resetForm();
  }

  private resetForm() {
    this.username = '';
    this.password = '';
    this.repeatPassword = '';
    this.email = '';
    this.role = '';
  }

  close(callback?: () => void) {
    this.visible = false;
    setTimeout(() => {
      callback?.();
      this.cancel.emit();
    }, 200);
  }
}
