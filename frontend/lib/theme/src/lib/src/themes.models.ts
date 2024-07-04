
export type GsColorType = 
    'primary' | 
    'primary-contrast' | 
    'secondary' |
    'secondary-contrast' |
    'error' | 
    'error-contrast' | 
    'warn' |
    'warn-contrast' |
    'success' |
    'success-contrast' |
    'text' |
    'text-contrast' |
    'background' |
    'background-contrast' |
    'surface' |
    'surface-contrast' |
    'shadow' |
    'shadow-contrast' |
    'disabled' |
    'disabled-contrast';

export type GsColorState = 
    '' |
    'hover' | 
    'active';

export type GsTheme = 
    'light' | 
    'dark';

export interface GsColor {
    type: GsColorType,
    state: GsColorState
}
