import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router, private authService: AuthService) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const token = this.authService.getToken();

    if (!token) {
      this.router.navigate(['/']);
      return false;
    }

    const allowedRoles = route.data['rolesAllowed'];

    if (!allowedRoles) {
      return true;
    }

    const userRole = this.authService.getRole();

    if (allowedRoles.includes(userRole)) {
      return true;
    } else {
      this.router.navigate(['/home']);
      return false;
    }
  }
}
