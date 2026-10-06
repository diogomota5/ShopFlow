import { Component, inject } from '@angular/core';
import { ConfirmDialogService } from '../../core/services/confirm-dialog.service';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [],
  template: `
    @if (dialog.isOpen() && dialog.options(); as opts) {
      <div class="confirm-backdrop" (click)="dialog.cancelAction()">
        <div class="confirm-container" (click)="$event.stopPropagation()">
          <!-- Icon Context Header -->
          <div class="confirm-icon-wrapper" [class]="'confirm-icon-wrapper--' + opts.type">
            @switch (opts.type) {
              @case ('danger') {
                <svg
                  width="24"
                  height="24"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  />
                </svg>
              }
              @case ('warning') {
                <svg
                  width="24"
                  height="24"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  />
                </svg>
              }
              @default {
                <svg
                  width="24"
                  height="24"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                  />
                </svg>
              }
            }
          </div>

          <!-- Content Description -->
          <div class="confirm-content">
            <h3 class="confirm-title">{{ opts.title }}</h3>
            <p class="confirm-message">{{ opts.message }}</p>
          </div>

          <!-- Interactive Actions -->
          <div class="confirm-actions">
            <button
              type="button"
              class="btn btn-ghost confirm-btn-cancel"
              (click)="dialog.cancelAction()"
            >
              {{ opts.cancelText }}
            </button>
            <button
              type="button"
              class="btn"
              [class.btn-primary]="opts.type === 'primary'"
              [class.btn-danger]="opts.type === 'danger'"
              [class.btn-accent]="opts.type === 'warning'"
              (click)="dialog.confirmAction()"
            >
              {{ opts.confirmText }}
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [
    `
      .confirm-backdrop {
        position: fixed;
        inset: 0;
        z-index: 10000;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--surface-overlay, rgba(0, 0, 0, 0.55));
        backdrop-filter: blur(6px);
        -webkit-backdrop-filter: blur(6px);
        animation: backdrop-in 0.2s ease-out both;
      }

      @keyframes backdrop-in {
        from {
          opacity: 0;
        }
        to {
          opacity: 1;
        }
      }

      .confirm-container {
        width: 100%;
        max-width: 400px;
        margin: 1.5rem;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 2.25rem 2rem 1.75rem;
        border-radius: var(--radius-xl, 24px);
        background: var(--surface-card, #fff);
        box-shadow:
          0 20px 50px rgba(0, 0, 0, 0.3),
          0 0 0 1px var(--border, rgba(0, 0, 0, 0.08));
        text-align: center;
        animation: container-in 0.3s cubic-bezier(0.34, 1.56, 0.64, 1) both;
      }

      @keyframes container-in {
        from {
          opacity: 0;
          transform: scale(0.9) translateY(20px);
        }
        to {
          opacity: 1;
          transform: scale(1) translateY(0);
        }
      }

      .confirm-icon-wrapper {
        width: 56px;
        height: 56px;
        border-radius: var(--radius-full, 9999px);
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 1.25rem;
      }

      .confirm-icon-wrapper--primary {
        background: var(--primary-alpha, rgba(160, 32, 240, 0.12));
        color: var(--primary, #a020f0);
      }

      .confirm-icon-wrapper--danger {
        background: var(--danger-alpha, rgba(255, 0, 0, 0.12));
        color: var(--danger, #ff3b30);
        animation: shake 0.5s ease-in-out;
      }

      .confirm-icon-wrapper--warning {
        background: var(--warning-alpha, rgba(255, 204, 0, 0.12));
        color: var(--warning, #ffcc00);
      }

      @keyframes shake {
        0%,
        100% {
          transform: translateX(0);
        }
        20%,
        60% {
          transform: translateX(-4px);
        }
        40%,
        80% {
          transform: translateX(4px);
        }
      }

      .confirm-content {
        margin-bottom: 2rem;
      }

      .confirm-title {
        font-family: var(--font-heading);
        font-size: 1.25rem;
        font-weight: 700;
        color: var(--text-primary, #1a1a1d);
        margin-bottom: 0.5rem;
      }

      .confirm-message {
        font-family: var(--font-body);
        font-size: 0.9rem;
        line-height: 1.5;
        color: var(--text-secondary, #5c5668);
      }

      .confirm-actions {
        width: 100%;
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 0.75rem;
      }

      .confirm-actions .btn {
        padding: 0.75rem 1rem;
        font-size: 0.9rem;
        font-weight: 600;
        border-radius: var(--radius-md, 12px);
      }

      .confirm-btn-cancel {
        border: 1px solid var(--border, #e4dced);
        color: var(--text-secondary);
      }

      .confirm-btn-cancel:hover {
        background: var(--surface-raised, #efe8f7);
      }

      /* Modificadores Light/Dark mode via data-theme */
      :host-context([data-theme='light']) .confirm-btn-cancel {
        border-color: var(--border, #e4dced);
      }
    `,
  ],
})
export class ConfirmDialogComponent {
  protected readonly dialog = inject(ConfirmDialogService);
}
