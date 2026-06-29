import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast { id: string; message: string; type: 'success' | 'error' | 'info' | 'warning'; }

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toastsSubject = new BehaviorSubject<Toast[]>([]);
  toasts$ = this.toastsSubject.asObservable();

  private add(message: string, type: Toast['type']): void {
    const id = Date.now().toString();
    const toasts = [...this.toastsSubject.value, { id, message, type }];
    this.toastsSubject.next(toasts);
    setTimeout(() => this.remove(id), 4000);
  }

  success(message: string): void { this.add(message, 'success'); }
  error(message: string): void { this.add(message, 'error'); }
  info(message: string): void { this.add(message, 'info'); }
  warning(message: string): void { this.add(message, 'warning'); }
  remove(id: string): void { this.toastsSubject.next(this.toastsSubject.value.filter(t => t.id !== id)); }
}
