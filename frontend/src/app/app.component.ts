import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationError } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: '<router-outlet />'
})
export class AppComponent implements OnInit {
  private router = inject(Router);
  ngOnInit(): void {
    this.router.events.pipe(filter(e => e instanceof NavigationError)).subscribe((e: any) => {
      console.error('NAV ERROR:', e.error?.message ?? e.error ?? e);
    });
  }
}
