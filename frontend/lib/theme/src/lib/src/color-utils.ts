
import { colord } from 'colord';

export function setAlpha(color: string, newAlpha: number): string {
    return colord(color)
        .alpha(newAlpha)
        .toHex();
}
