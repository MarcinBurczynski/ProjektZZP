import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-category-popup',
  templateUrl: './add-category-popup.component.html',
  styleUrls: ['./add-category-popup.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class AddCategoryPopupComponent {
  @Input() visible: boolean = false;
  @Output() confirm = new EventEmitter<string>();
  @Output() cancel = new EventEmitter<void>();

  categoryName: string = '';

  onConfirm() {
    if (this.categoryName.trim()) {
      this.confirm.emit(this.categoryName.trim());
      this.categoryName = '';
    }
  }

  onCancel() {
    this.cancel.emit();
    this.categoryName = '';
  }
}
