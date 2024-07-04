import { Component, computed, ElementRef, inject, input, output, ViewChild } from '@angular/core';
import { MenuItem } from './menu.models';
import { ComponentNotificationService } from '@frontend/common-services';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map } from 'rxjs';
import { GsColorType, GsTheme } from '@frontend/theme';

@Component({
  selector: 'gs-menu',
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.scss',
})
export class GsMenuComponent {
  private componentNotificationService: ComponentNotificationService = inject(ComponentNotificationService);

  @ViewChild('', {read: ElementRef})
  public childElement?: ElementRef;

  public menuItems = input<MenuItem[]>([])
  public menuTextColor = input<GsColorType>('text');
  public menuSurfaceColor = input<GsColorType>('background');
  public menuHighlightColorOrigin = input<GsColorType>('text');
  public onMenuSelect = output<string>();

  protected colorVars = computed(() => 
    {
      return `--comp-menu-background: var(--gs-${this.menuSurfaceColor()});`;
    }
  );

  protected themeSignal = toSignal(this.componentNotificationService.refreshThemeCalculations$.pipe(
    filter((theme): theme is GsTheme => theme !== undefined),
    map(theme => `${theme}-theme`),
  ));

  protected onSelect(id: string, onClick: Function | undefined): void {
    if(onClick) {
      onClick();
    }
    this.onMenuSelect.emit(id);
  }
}
