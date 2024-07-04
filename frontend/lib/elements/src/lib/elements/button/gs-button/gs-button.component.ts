import { Component, computed, ElementRef, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { getColorCssVars, getColorWithAlpha, GsColorType } from '@frontend/theme';
import { ComponentNotificationService } from '@frontend/common-services';

type ButtonTypes = 'outlined' | 'filled' | 'basic' | 'unshaped';

@Component({
  selector: 'gs-button',
  templateUrl: './gs-button.component.html',
  styleUrl: './gs-button.component.scss',
})
export class GsButtonComponent {
  private elementRef: ElementRef = inject(ElementRef);
  private componentNotificationService: ComponentNotificationService = inject(ComponentNotificationService);

  public variant = input<ButtonTypes>('filled');
  public color = input<GsColorType>('primary');
  public textColor = input<GsColorType>('text');
  public disabled = input<string>('');

  protected refreshTheme = toSignal(this.componentNotificationService.refreshThemeCalculations$);

  protected colorVars = computed(() => {
    this.refreshTheme();
     
    return getColorCssVars("base", this.color()) + 
      getColorCssVars("text", this.textColor()) +
      getColorWithAlpha('background-low', {
        type: this.color(),
        state: 'hover'
      }, 0.2, this.elementRef) + 
      getColorWithAlpha('background-high',{
        type: this.color(),
        state: 'active'
      }, 0.4, this.elementRef);
  });
}
