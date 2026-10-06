import { Component, inject, signal } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  showPassword = signal(false);
  loading = signal(false);
  error = signal('');
  failedAttempts = signal(0);
  cooldown = signal(0);
  private cooldownTimer?: ReturnType<typeof setInterval>;
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  get email(): AbstractControl {
    return this.form.get('email')!;
  }
  get password(): AbstractControl {
    return this.form.get('password')!;
  }
  get isCoolingDown(): boolean {
    return this.cooldown() > 0;
  }

  submit(): void {
    if (this.form.invalid || this.isCoolingDown || this.loading()) return;

    this.loading.set(true);
    this.error.set('');

    this.auth.login({ email: this.email.value, password: this.password.value }).subscribe({
      next: () => {
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        // Validate returnUrl is a relative path to prevent open redirect
        const safeUrl =
          returnUrl && returnUrl.startsWith('/') && !returnUrl.startsWith('//') ? returnUrl : '/';
        this.router.navigateByUrl(safeUrl);
      },
      error: (err: any) => {
        this.loading.set(false);
        this.failedAttempts.update((v) => v + 1);

        if (err.status === 401 || err.status === 403) {
          this.error.set(err.error?.message || 'E-mail ou senha inválidos. Tente novamente.');
        } else if (err.status === 0) {
          this.error.set('Não foi possível conectar ao servidor. Verifique sua conexão.');
        } else {
          this.error.set(err.error?.message || 'Ocorreu um erro inesperado. Tente mais tarde.');
        }

        // Brute-force cooldown: 3 failed → 30s lockout
        if (this.failedAttempts() >= 3) {
          this.startCooldown(30);
        }
      },
    });
  }

  private startCooldown(seconds: number): void {
    this.cooldown.set(seconds);
    clearInterval(this.cooldownTimer);
    this.cooldownTimer = setInterval(() => {
      this.cooldown.update((v) => {
        if (v <= 1) {
          clearInterval(this.cooldownTimer);
          this.failedAttempts.set(0);
          return 0;
        }
        return v - 1;
      });
    }, 1000);
  }
}
