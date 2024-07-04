import { ElementRef } from '@angular/core';
import { GsColorType, GsColor } from "./themes.models";
import { setAlpha } from './color-utils';

export function getColorCssVars(prefix: string, color: GsColorType): string {
    return `--comp-${prefix}: var(--gs-${color});
    --comp-${prefix}-hover: var(--gs-${color}-hover);
    --comp-${prefix}-active: var(--gs-${color}-active);`
}

export function getColorWithAlpha(prefix: string, gsColor: GsColor, alpha: number, elementRef: ElementRef,): string {
    const nativeElement = elementRef.nativeElement;
    const colorProp = `--gs-${gsColor.type}` + (gsColor.state ? `-${gsColor.state}` : '');
    const computedColor = window.getComputedStyle(nativeElement).getPropertyValue(colorProp);

    const colorAlphaed = setAlpha(computedColor, alpha);
    return `--comp-${prefix}: ${colorAlphaed};`;
}
