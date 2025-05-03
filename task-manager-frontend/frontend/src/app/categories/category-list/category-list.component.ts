import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Category, User } from '../../../utils/interfaces';
import { fetchCategories, fetchUsers } from '../../../utils/fetching_helper';
import { EditCategoryPopupComponent } from '../../components/edit-category-popup/edit-category-popup.component';
import { AuthService } from '../../auth/auth.service';
import { DeleteCategoryPopupComponent } from '../../components/delete-category-popup/delete-category-popup.component';
import { deleteCategory } from '../../../utils/deleting_helper';
import { putCategory } from '../../../utils/putting_helper';
import { AddCategoryPopupComponent } from '../../components/add-category-popup/add-category-popup.component';
import { postCategory } from '../../../utils/posting_helper';
import { Router } from '@angular/router';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css'],
  imports: [
    CommonModule,
    EditCategoryPopupComponent,
    DeleteCategoryPopupComponent,
    AddCategoryPopupComponent,
  ],
})
export class CategoryListComponent implements OnInit {
  constructor(private router: Router, private authService: AuthService) {}

  categories: Category[] = [];
  users: User[] = [];
  username: string | null = '';
  role: string | null = '';

  showDeletePopup: boolean = false;
  showEditPopup: boolean = false;
  showAddCategoryPopup: boolean = false;

  categoryToDelete: Category | null = null;
  categoryToEdit: Category | null = null;

  isAdminOrModerator: boolean = false;

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.role = this.authService.getRole();
    this.isAdminOrModerator = this.role !== 'USER';
    fetchCategories().then((categories) => (this.categories = categories));
    if (this.isAdminOrModerator)
      fetchUsers().then((users) => (this.users = users));
  }

  editCategory(category: Category): void {
    this.categoryToEdit = { ...category };
    this.showEditPopup = true;
  }

  handleConfirmEdit(updatedCategory: Category): void {
    ``;
    putCategory(
      updatedCategory.id,
      updatedCategory.name,
      updatedCategory.userId
    ).then((success) => {
      if (success) {
        const index = this.categories.findIndex(
          (c) => c.id === updatedCategory.id
        );
        if (index !== -1) {
          this.categories[index] = updatedCategory;
        }
      }
    });
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

  handleConfirmDelete(): void {
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

  handleCancelDelete(): void {
    this.showDeletePopup = false;
    this.categoryToDelete = null;
  }

  postNewCategory(name: string) {
    postCategory(name).then(() => {
      fetchCategories().then((categories) => {
        this.categories = categories;
      });
    });
  }

  getUsernameFromId(userId: number): string {
    return this.users.find((user) => user.id === userId)?.username || '';
  }

  logout() {
    this.authService.logout();
  }

  goToHome() {
    this.router.navigate(['/home']);
  }
}
