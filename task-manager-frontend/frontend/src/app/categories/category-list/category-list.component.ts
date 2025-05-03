import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Category, User } from '../../../utils/interfaces';
import { fetchCategories, fetchUsers } from '../../../utils/fetching_helper';
import { EditCategoryPopupComponent } from '../../components/edit-category-popup/edit-category-popup.component';
import { getRole } from '../../../utils/role_getter';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css'],
  imports: [CommonModule,EditCategoryPopupComponent],
})
export class CategoryListComponent implements OnInit {
  categories: Category[] = [];
  users: User[] = [];

  showDeletePopup: boolean = false;
  showEditPopup: boolean = false;

  categoryToDelete: Category | null = null;
  categoryToEdit: Category | null = null;

  isAdminOrModerator = getRole()!=="USER"; // replace with real permission check

  constructor() {}

  ngOnInit(): void {
    fetchCategories().then((categories) => (this.categories = categories));
    if(this.isAdminOrModerator)fetchUsers().then((users) => (this.users = users));
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

  deleteCategory(category: any): void {
    this.categoryToDelete = category;
    this.showDeletePopup = true;
  }

  confirmDelete(): void {
//     this.categories = this.categories.filter(
//       (category) => category.id !== this.categoryToDelete.id
//     );
//     this.showDeletePopup = false;
  }

  cancelDelete(): void {
    this.showDeletePopup = false;
  }

  getUsernameFromId(userId: number): string {
    return this.users.find((user) => user.id === userId)?.username || '';
  }
}
