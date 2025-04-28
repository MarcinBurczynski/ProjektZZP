import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { fetchCategories } from '../../../utils/fetching_helper';
import { Category } from '../../../utils/interfaces';

@Component({
  selector: 'app-category-list',
  templateUrl: './category-list.component.html',
  styleUrls: ['./category-list.component.css'],
  standalone: true,
  imports: [CommonModule],
})
export class CategoryListComponent implements OnInit {
  categories: Category[] = [];

  constructor() {}

  ngOnInit() {
    fetchCategories().then(categories => this.categories = categories);
  }
}
