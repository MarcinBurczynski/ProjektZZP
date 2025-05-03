import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Category, User } from '../../../utils/interfaces';
import { fetchCategories, fetchUsers } from '../../../utils/fetching_helper';
import { EditCategoryPopupComponent } from '../../components/edit-category-popup/edit-category-popup.component';
import { AuthService } from '../../auth/auth.service';
import { DeleteCategoryPopupComponent } from '../../components/delete-category-popup/delete-category-popup.component';
import { deleteCategory } from '../../../utils/deleting_helper';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css'],
  imports: [
    CommonModule,
    EditCategoryPopupComponent,
    DeleteCategoryPopupComponent,
  ],
})
export class CategoryListComponent implements OnInit {
  constructor(private authService: AuthService) {}

  categories: Category[] = [];
  users: User[] = [];

  showDeletePopup: boolean = false;
  showEditPopup: boolean = false;

  categoryToDelete: Category | null = null;
  categoryToEdit: Category | null = null;

  isAdminOrModerator: boolean = false;

  ngOnInit(): void {
    fetchCategories().then((categories) => (this.categories = categories));
    this.isAdminOrModerator = this.authService.getRole() !== 'USER';
    if (this.isAdminOrModerator)
      fetchUsers().then((users) => (this.users = users));
  }

  editCategory(category: Category): void {
    this.categoryToEdit = { ...category };
    this.showEditPopup = true;
  }

  handleConfirmEdit(updatedCategory: Category): void {
    const index = this.categories.findIndex((c) => c.id === updatedCategory.id);
    if (index !== -1) {
      this.categories[index] = updatedCategory;
    }
    this.showEditPopup = false;
    this.categoryToEdit = null;
  }

  handleCancelEdit(): void {
    this.showEditPopup = false;
    this.categoryToEdit = null;
  }

  deleteCategory(category: Category): void {
    this.categoryToDelete = category;
    this.showDeletePopup = true;
  }

  confirmDelete(): void {
    if (!this.categoryToDelete) return;
    const toDeleteId: number = this.categoryToDelete.id;
    deleteCategory(this.categoryToDelete.id).then((success) => {
      if (success) {
        const index = this.categories.findIndex((c) => c.id === toDeleteId);
        if (index !== -1) {
          this.categories.splice(index, 1);
        }
      }
    });
    this.showDeletePopup = false;
    this.categoryToDelete = null;
  }

  getUsernameFromId(userId: number): string {
    return this.users.find((user) => user.id === userId)?.username || '';
  }
}
