import { Injectable, signal } from '@angular/core';

export interface ConfirmDialogOptions {
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  type?: 'primary' | 'danger' | 'warning';
}

@Injectable({ providedIn: 'root' })
export class ConfirmDialogService {
  private resolveFn: ((value: boolean) => void) | null = null;

  options = signal<ConfirmDialogOptions | null>(null);
  isOpen = signal<boolean>(false);

  /**
   * Opens the confirmation dialog and returns a Promise that resolves to `true` (confirmed) or `false` (canceled).
   */
  confirm(options: ConfirmDialogOptions): Promise<boolean> {
    this.options.set({
      confirmText: 'Confirmar',
      cancelText: 'Cancelar',
      type: 'primary',
      ...options,
    });
    this.isOpen.set(true);

    return new Promise<boolean>((resolve) => {
      this.resolveFn = resolve;
    });
  }

  confirmAction(): void {
    if (this.resolveFn) {
      this.resolveFn(true);
    }
    this.close();
  }

  cancelAction(): void {
    if (this.resolveFn) {
      this.resolveFn(false);
    }
    this.close();
  }

  private close(): void {
    this.isOpen.set(false);
    this.options.set(null);
    this.resolveFn = null;
  }
}
