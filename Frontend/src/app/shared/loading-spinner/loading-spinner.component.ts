import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { LoadingService } from '../../core/services/loading.service';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [AsyncPipe],
  template: `
    @if (loading.loading$ | async) {
      <div class="loading-backdrop">
        <div class="loading-container">
          <div class="loading-ring">
            <svg viewBox="0 0 50 50">
              <circle class="loading-ring__track" cx="25" cy="25" r="20" />
              <circle class="loading-ring__fill" cx="25" cy="25" r="20" />
            </svg>
          </div>
          <span class="loading-text">Processando...</span>
        </div>
      </div>
    }
  `,
  styles: [
    `
      .loading-backdrop {
        position: fixed;
        inset: 0;
        z-index: 9999;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--surface-overlay, rgba(0, 0, 0, 0.55));
        backdrop-filter: blur(6px);
        -webkit-backdrop-filter: blur(6px);
        animation: backdrop-in 0.25s ease-out both;
      }

      @keyframes backdrop-in {
        from {
          opacity: 0;
        }
        to {
          opacity: 1;
        }
      }

      .loading-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1.25rem;
        padding: 2.5rem 3rem;
        border-radius: var(--radius-xl, 24px);
        background: var(--surface-card, #fff);
        box-shadow:
          0 8px 40px rgba(0, 0, 0, 0.18),
          0 0 0 1px var(--border, rgba(0, 0, 0, 0.06));
        animation: container-in 0.35s cubic-bezier(0.34, 1.56, 0.64, 1) both;
      }

      @keyframes container-in {
        from {
          opacity: 0;
          transform: scale(0.85) translateY(12px);
        }
        to {
          opacity: 1;
          transform: scale(1) translateY(0);
        }
      }

      .loading-ring {
        width: 56px;
        height: 56px;
        position: relative;
      }

      .loading-ring svg {
        width: 100%;
        height: 100%;
        animation: ring-rotate 1.4s linear infinite;
      }

      .loading-ring__track {
        fill: none;
        stroke: var(--border, #e4dced);
        stroke-width: 4;
      }

      .loading-ring__fill {
        fill: none;
        stroke: var(--primary, #a020f0);
        stroke-width: 4;
        stroke-linecap: round;
        stroke-dasharray: 90, 150;
        stroke-dashoffset: 0;
        animation: ring-dash 1.4s ease-in-out infinite;
        filter: drop-shadow(0 0 6px var(--primary, #a020f0));
      }

      @keyframes ring-rotate {
        to {
          transform: rotate(360deg);
        }
      }

      @keyframes ring-dash {
        0% {
          stroke-dasharray: 1, 200;
          stroke-dashoffset: 0;
        }
        50% {
          stroke-dasharray: 90, 200;
          stroke-dashoffset: -35;
        }
        100% {
          stroke-dasharray: 90, 200;
          stroke-dashoffset: -125;
        }
      }

      .loading-text {
        font-family: var(--font-body, 'Inter', sans-serif);
        font-size: 0.875rem;
        font-weight: 500;
        color: var(--text-secondary, #5c5668);
        letter-spacing: 0.02em;
        animation: text-pulse 2s ease-in-out infinite;
      }

      @keyframes text-pulse {
        0%,
        100% {
          opacity: 0.6;
        }
        50% {
          opacity: 1;
        }
      }
    `,
  ],
})
export class LoadingSpinnerComponent {
  protected readonly loading = inject(LoadingService);
}
