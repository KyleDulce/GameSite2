import { Component, computed, ElementRef, inject, input } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ComponentNotificationService } from '@frontend/common-services';
import { getColorCssVars, getColorWithAlpha, GsColorType } from '@frontend/theme';

type IconButtonTypes = 'basic' | 'filled';

@Component({
  selector: 'gs-icon-button',
  templateUrl: './icon-button.component.html',
  styleUrl: './icon-button.component.scss',
})
export class IconButtonComponent {
  private elementRef: ElementRef = inject(ElementRef);
  private componentNotificationService: ComponentNotificationService = inject(ComponentNotificationService);
  
  public variant = input<IconButtonTypes>('basic');
  public color = input<GsColorType>('text');
  public contrastColor = input<GsColorType>('text-contrast');
  public disabled = input<string>('');

  protected refreshTheme = toSignal(this.componentNotificationService.refreshThemeCalculations$);

  protected colorVars = computed(() => {
    this.refreshTheme();

    return getColorCssVars("base", this.color()) + 
      getColorCssVars("base-contrast", this.contrastColor()) + 
      getColorWithAlpha('background-low',{
        type: this.color(),
        state: 'hover'
      }, 0.2, this.elementRef) +
      getColorWithAlpha('background-high',{
        type: this.color(),
        state: 'active'
      }, 0.4, this.elementRef);
  });
}
