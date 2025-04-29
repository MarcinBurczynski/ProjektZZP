import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Category, User } from '../../../utils/interfaces';
import { fetchCategories, fetchUsers } from '../../../utils/fetching_helper';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css'],
  imports: [CommonModule],
})
export class CategoryListComponent implements OnInit {
  categories: Category[] = [];
  users: User[] = [];

  showDeletePopup: boolean = false;
  categoryToDelete: any = null;

  constructor() {}

  ngOnInit(): void {
    fetchCategories().then((categories) => (this.categories = categories));
    fetchUsers().then((users) => (this.users = users));
  }

  editCategory(category: any): void {
    console.log('Edytowanie kategorii:', category);
  }

  deleteCategory(category: any): void {
    this.categoryToDelete = category;
    this.showDeletePopup = true;
  }

  confirmDelete(): void {
    this.categories = this.categories.filter(
      (category) => category.id !== this.categoryToDelete.id
    );
    this.showDeletePopup = false;
  }

  cancelDelete(): void {
    this.showDeletePopup = false;
  }

  getUsernameFromId(userId: number): string {
    return this.users.find((user) => user.id === userId)?.username || '';
  }
}
