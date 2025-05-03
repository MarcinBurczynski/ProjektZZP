import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './auth/auth.guard';
import { CategoryListComponent } from './categories/category-list/category-list.component';
import { TaskListComponent } from './tasks/task-list/task-list.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  {
    path: 'categories',
    component: CategoryListComponent,
    canActivate: [AuthGuard],
  },
  { path: 'tasks', component: TaskListComponent, canActivate: [AuthGuard] },
];
