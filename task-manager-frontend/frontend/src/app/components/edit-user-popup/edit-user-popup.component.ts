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
import {
  trigger,
  style,
  animate,
  transition,
  query,
} from '@angular/animations';
import { User } from '../../../utils/interfaces';

@Component({
  selector: 'app-edit-user-popup',
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-user-popup.component.html',
  styleUrl: './edit-user-popup.component.css',
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
export class EditUserPopupComponent {
  @Input() visible: boolean = false;
  @Input() user: User | null = null;

  @Output() confirm = new EventEmitter<User>();
  @Output() cancel = new EventEmitter<void>();

  username: string = '';
  changePassword: boolean = false;
  password: string = '';
  repeatPassword: string = '';
  email: string = '';
  role: string = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['user'] && this.user) {
      this.username = this.user.username;
      this.password = this.user.password;
      this.email = this.user.email;
      this.role = this.user.role;
    }
  }

  onConfirm(): void {
    if (!this.user) return;

    this.confirm.emit({
      ...this.user,
      username: this.username,
      ...(this.password !== '' ? { password: this.password } : {}),
      email: this.email,
      role: this.role,
    });

    this.visible = false;
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
