import { Component, OnInit, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { FooterComponent } from './shared/footer/footer.component';
import { LoadingSpinnerComponent } from './shared/loading-spinner/loading-spinner.component';
import { ConfirmDialogComponent } from './shared/confirm-dialog/confirm-dialog.component';
import { CookieConsentComponent } from './shared/cookie-consent/cookie-consent.component';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    NavbarComponent,
    FooterComponent,
    LoadingSpinnerComponent,
    ConfirmDialogComponent,
    CookieConsentComponent,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  private auth = inject(AuthService);
  private router = inject(Router);

  showChrome = signal(true);

  ngOnInit(): void {
    this.auth.initSession();
    this.updateChrome(this.router.url);
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe((e) => this.updateChrome(e.urlAfterRedirects));
  }

  private updateChrome(url: string): void {
    const path = url.split('?')[0];
    const isAuth = ['/login', '/register'].includes(path);
    const isAdmin = path.startsWith('/admin');
    this.showChrome.set(!isAuth && !isAdmin);
  }
}
