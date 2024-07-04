import { Component, effect, inject, model, output, signal } from '@angular/core';
import { ComponentNotificationService } from '@frontend/common-services';
import { GsTheme } from '@frontend/theme';

export interface ThemeSettings {
  theme: GsTheme
}

@Component({
  selector: 'tester-controller',
  templateUrl: './controller.component.html',
  styleUrl: './controller.component.scss',
})
export class ControllerComponent {
  public themeSettings = output<ThemeSettings>();

  protected selectedTheme = signal<GsTheme>('light');
  protected fontSelector = model("16");

  constructor() {
    effect(() => {
      this.themeSettings.emit({
        theme: this.selectedTheme()
      });
    });

    effect(() => {
      const root = document.body.parentElement;
      if(root?.style) {
        root.style.fontSize = `${this.fontSelector()}px`;
      }
    });
  }
}
