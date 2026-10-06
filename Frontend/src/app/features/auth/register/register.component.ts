import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  Validators,
  ReactiveFormsModule,
  AbstractControl,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

const mustMatch = (ctrl: AbstractControl): ValidationErrors | null => {
  const pass = ctrl.get('password');
  const confirm = ctrl.get('confirmPassword');
  if (!pass || !confirm) return null;
  return pass.value !== confirm.value ? { mustMatch: true } : null;
};

const passwordStrength = (ctrl: AbstractControl): ValidationErrors | null => {
  const val: string = ctrl.value ?? '';
  if (!val) return null;
  const hasUpper = /[A-Z]/.test(val);
  const hasNumber = /\d/.test(val);
  const long = val.length >= 8;
  return hasUpper && hasNumber && long ? null : { weakPassword: true };
};

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  showPassword = signal(false);
  loading = signal(false);
  error = signal('');
  success = signal(false);

  form = this.fb.group(
    {
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', [Validators.maxLength(20)]],
      password: ['', [Validators.required, Validators.minLength(6), passwordStrength]],
      confirmPassword: ['', Validators.required],
    },
    { validators: mustMatch },
  );

  get name(): AbstractControl {
    return this.form.get('name')!;
  }
  get email(): AbstractControl {
    return this.form.get('email')!;
  }
  get telephone(): AbstractControl {
    return this.form.get('telephone')!;
  }
  get password(): AbstractControl {
    return this.form.get('password')!;
  }
  get confirmPassword(): AbstractControl {
    return this.form.get('confirmPassword')!;
  }

  get passwordStrengthLevel(): number {
    const val: string = this.password.value ?? '';
    let score = 0;
    if (val.length >= 6) score++;
    if (val.length >= 10) score++;
    if (/[A-Z]/.test(val)) score++;
    if (/\d/.test(val)) score++;
    if (/[^A-Za-z0-9]/.test(val)) score++;
    return score; // 0-5
  }

  get strengthLabel(): string {
    const l = this.passwordStrengthLevel;
    if (l <= 1) return 'Fraca';
    if (l <= 3) return 'Média';
    return 'Forte';
  }

  get strengthLevelKey(): 'weak' | 'medium' | 'strong' {
    const l = this.passwordStrengthLevel;
    if (l <= 1) return 'weak';
    if (l <= 3) return 'medium';
    return 'strong';
  }

  submit(): void {
    if (this.form.invalid || this.loading()) return;
    this.loading.set(true);
    this.error.set('');

    const { name, email, password, telephone } = this.form.value;
    this.auth
      .register({
        name: name!,
        email: email!,
        password: password!,
        telephone: telephone ?? undefined,
      })
      .subscribe({
        next: () => {
          this.success.set(true);
          setTimeout(() => this.router.navigate(['/login']), 4000);
        },
        error: (err: any) => {
          this.loading.set(false);
          if (err.status === 409 || err.status === 400) {
            this.error.set(err.error?.message ?? 'Este e-mail já pode estar cadastrado.');
          } else {
            this.error.set('Falha no cadastro. Tente novamente.');
          }
        },
      });
  }
}
