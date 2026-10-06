import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// Protects authenticated-only routes
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    return true;
  }

  // Preserve intended destination for post-login redirect
  // Validate returnUrl to only allow relative paths (prevents open redirect)
  const safeUrl = state.url.startsWith('/') ? state.url : '/';
  return router.createUrlTree(['/login'], { queryParams: { returnUrl: safeUrl } });
};
