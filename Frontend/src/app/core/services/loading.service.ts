import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private activeRequests = 0;
  private readonly _loading = new BehaviorSubject<boolean>(false);

  /** Observable that emits `true` while at least one HTTP request is in-flight. */
  readonly loading$ = this._loading.asObservable();

  show(): void {
    this.activeRequests++;
    this._loading.next(true);
  }

  hide(): void {
    this.activeRequests = Math.max(this.activeRequests - 1, 0);
    if (this.activeRequests === 0) {
      this._loading.next(false);
    }
  }
}
