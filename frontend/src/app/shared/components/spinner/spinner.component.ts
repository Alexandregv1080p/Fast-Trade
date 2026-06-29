import { Component } from '@angular/core';
@Component({
  selector: 'app-spinner',
  standalone: true,
  template: `<div class="spinner"><div class="spinner__ring"></div></div>`,
  styles: [`
    .spinner { display: flex; align-items: center; justify-content: center; padding: 40px; }
    .spinner__ring {
      width: 40px; height: 40px; border-radius: 50%;
      border: 4px solid var(--color-border);
      border-top-color: var(--color-primary);
      animation: spin .8s linear infinite;
    }
    @keyframes spin { to { transform: rotate(360deg); } }
  `]
})
export class SpinnerComponent {}
