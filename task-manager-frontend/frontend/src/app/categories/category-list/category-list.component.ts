import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import apiClient from '../../../environments/axios';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class CategoryListComponent implements OnInit {
  categories: any[] = [];

  constructor() {}

  ngOnInit() {
    this.fetchCategories();
  }

  async fetchCategories() {
    try {
      const response = await apiClient.get('/api/categories');
      if (response.status === 200) {
        this.categories = response.data;
      } else {
        console.error('Failed to fetch categories:', response.status);
      }
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  }
}
