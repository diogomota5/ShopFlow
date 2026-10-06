import { Component, inject, HostListener, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CartService } from '../../core/services/cart.service';
import { ThemeService } from '../../core/services/theme.service';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  protected cart = inject(CartService);
  protected theme = inject(ThemeService);

  searchQuery = '';
  userMenuOpen = signal(false);
  mobileNavOpen = signal(false);

  get isLoggedIn() {
    return this.auth.isLoggedIn();
  }
  get isAdmin() {
    return this.auth.isAdmin();
  }
  get cartCount() {
    return this.cart.cartCount();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu-wrapper')) {
      this.userMenuOpen.set(false);
    }
  }

  search(): void {
    const q = this.searchQuery.trim();
    if (q) {
      this.router.navigate(['/products'], { queryParams: { name: q } });
      this.searchQuery = '';
    }
  }

  logout(): void {
    this.auth.logout();
    this.userMenuOpen.set(false);
  }

  toggleUserMenu(): void {
    this.userMenuOpen.update((v) => !v);
  }

  toggleMobileNav(): void {
    this.mobileNavOpen.update((v) => !v);
  }
}
