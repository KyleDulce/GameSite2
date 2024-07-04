import { Component, inject, signal } from '@angular/core';
import { MaterialModule } from './material.module';
import { AppModule } from './app.module';
import { ThemeSettings } from './controller/controller.component';
import { ComponentNotificationService } from '@frontend/common-services';
import { toObservable } from '@angular/core/rxjs-interop';
import { tap } from 'rxjs';

@Component({
  standalone: true,
  imports: [
    MaterialModule, 
    AppModule
  ],
  providers: [
    ComponentNotificationService
  ],
  selector: 'tester-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  private componentNotificationService: ComponentNotificationService = inject(ComponentNotificationService);

  public themeSwitcher = signal<ThemeSettings>({
    theme: 'light'
  });

  private themeSwitch$ = toObservable(this.themeSwitcher);

  constructor() {
    this.themeSwitch$.pipe(
      tap(themeSettings => this.componentNotificationService.refreshThemeCalculations$.next(themeSettings.theme))
    ).subscribe();
  }
}
