import { Injectable } from '@angular/core';

type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly KEY = 'sf_theme';
  private current: Theme;

  constructor() {
    const saved = localStorage.getItem(this.KEY) as Theme | null;
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    this.current = saved ?? (prefersDark ? 'dark' : 'light');
    this.apply(this.current);
  }

  get isDark(): boolean {
    return this.current === 'dark';
  }

  toggle(): void {
    this.current = this.current === 'dark' ? 'light' : 'dark';
    localStorage.setItem(this.KEY, this.current);
    this.apply(this.current);
  }

  private apply(theme: Theme): void {
    document.documentElement.setAttribute('data-theme', theme);
  }
}
