import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css'],
  imports: [CommonModule],
  animations: [
    trigger('toastAnimation', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(30px)' }),
        animate(
          '250ms ease-out',
          style({ opacity: 1, transform: 'translateY(0)' })
        ),
      ]),
      transition(':leave', [
        animate(
          '200ms ease-in',
          style({ opacity: 0, transform: 'translateY(30px)' })
        ),
      ]),
    ]),
  ],
})
export class ToastComponent implements OnInit {
  @Input() visible: boolean = false;
  @Input() message: string = '';
  @Input() type: 'success' | 'error' | 'info' = 'info';
  @Output() closed = new EventEmitter<void>();

  toastTypes = {
    success: { color: 'green', icon: 'pi-check-circle', header: 'Sukces!' },
    error: { color: 'red', icon: 'pi-times-circle', header: 'Błąd!' },
    info: { color: 'blue', icon: 'pi-info-circle', header: 'Informacja' },
  };

  ngOnInit() {
    setTimeout(() => this.close(), 5000);
  }

  close() {
    this.visible = false;
    setTimeout(() => this.closed.emit(), 200);
  }

  get toastStyle() {
    return { backgroundColor: this.toastTypes[this.type].color };
  }

  get icon() {
    return this.toastTypes[this.type].icon;
  }

  get header() {
    return this.toastTypes[this.type].header;
  }
}
