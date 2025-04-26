import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './auth/auth.guard';
import { RegisterComponent } from './register/register.component';
import { CategoryListComponent } from './categories/category-list/category-list.component';
//import { TasksComponent } from './tasks/task-list/task-list.component';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] }, // Chronienie strony home
  { path: 'register', component: RegisterComponent},
  { path: 'categories', component: CategoryListComponent },
  //{ path: 'tasks', component: TasksComponent },
];
