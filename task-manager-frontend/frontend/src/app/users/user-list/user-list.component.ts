import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../auth/auth.service';
import { AddUserPopupComponent } from '../../components/add-user-popup/add-user-popup.component';
import { EditUserPopupComponent } from '../../components/edit-user-popup/edit-user-popup.component';
import { DeleteElementPopupComponent } from '../../components/delete-element-popup/delete-element-popup.component';
import { fetchUsers } from '../../../utils/fetching_helper';
import { roleColors, rolesTranslations } from '../../../utils/roles';
import { putUser } from '../../../utils/putting_helper';
import { postUser } from '../../../utils/posting_helper';
import { deleteUser } from '../../../utils/deleting_helper';
import { User } from '../../../utils/interfaces';

@Component({
  selector: 'app-user-list',
  imports: [
    CommonModule,
    AddUserPopupComponent,
    EditUserPopupComponent,
    DeleteElementPopupComponent,
  ],
  templateUrl: './user-list.component.html',
  styleUrl: './user-list.component.css',
})
export class UserListComponent implements OnInit {
  constructor(private router: Router, private authService: AuthService) {}

  users: User[] = [];
  username: string | null = '';
  role: string | null = '';

  showAddUserPopup: boolean = false;
  showDeleteUserPopup: boolean = false;
  showEditUserPopup: boolean = false;
  userToDelete: User | null = null;
  userToEdit: User | null = null;
  isAdmin: boolean = false;

  async ngOnInit(): Promise<void> {
    this.username = this.authService.getUsername();
    this.role = this.authService.getRole();
    this.isAdmin = this.role === 'ADMIN';
    this.users = await fetchUsers();
  }

  editUser(user: User): void {
    this.userToEdit = { ...user };
    this.showEditUserPopup = true;
  }

  handleConfirmEdit(updatedUser: User): void {
    ``;
    putUser(
      updatedUser.id,
      updatedUser.username,
      updatedUser.password,
      updatedUser.email,
      updatedUser.role
    ).then((success) => {
      if (success) {
        const index = this.users.findIndex((u) => u.id === updatedUser.id);
        if (index !== -1) {
          this.users[index] = updatedUser;
        }
      }
    });
    this.showEditUserPopup = false;
    this.userToEdit = null;
  }

  handleCancelEdit(): void {
    this.showEditUserPopup = false;
    this.userToEdit = null;
  }

  deleteUser(user: User): void {
    this.userToDelete = user;
    this.showDeleteUserPopup = true;
  }

  handleConfirmDelete(): void {
    if (!this.userToDelete) return;
    const toDeleteId: number = this.userToDelete.id;
    deleteUser(this.userToDelete.id).then((success) => {
      if (success) {
        const index = this.users.findIndex((u) => u.id === toDeleteId);
        if (index !== -1) {
          this.users.splice(index, 1);
        }
      }
    });
    this.showDeleteUserPopup = false;
    this.userToDelete = null;
  }

  handleCancelDelete(): void {
    this.showDeleteUserPopup = false;
    this.userToDelete = null;
  }

  postNewUser(obj: {
    username: string;
    password: string;
    email: string;
    role: string;
  }) {
    postUser(obj.username, obj.password, obj.email, obj.role).then(() => {
      fetchUsers().then((users) => {
        this.users = users;
      });
    });
  }

  getRoleColor(role: string): string {
    return roleColors[role] || '#adb5bd';
  }

  getTranslatedRole(role: string): string {
    return rolesTranslations[role] || role;
  }

  logout() {
    this.authService.logout();
  }

  goToHome() {
    this.router.navigate(['/home']);
  }
}
