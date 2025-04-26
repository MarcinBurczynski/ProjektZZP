import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css'],
  standalone: true,
  imports: [CommonModule]
})
export class CategoryListComponent implements OnInit {
  categories: any[] = [];

  private token: string = 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0NTY2ODI0OSwiZXhwIjoxNzQ1NzU0NjQ5fQ.SJSoQbQDiLTKzpo4onzTBU2iU7N9eLkz1-OE-8ZcEVU';

  constructor() {}

  ngOnInit() {
    this.fetchCategories();
  }

  async fetchCategories() {
    try {
      const response = await fetch('http://localhost:8080/api/categories', {
        headers: {
          'Authorization': this.token,
          'Content-Type': 'application/json'
        }
      });
      if (response.ok) {
        this.categories = await response.json();
      } else {
        console.error('Failed to fetch categories:', response.status);
      }
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  }
}
