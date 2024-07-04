import { Injectable } from "@angular/core";
import { BehaviorSubject, Subject } from "rxjs";
import { GsTheme } from '@frontend/theme';

@Injectable()
export class ComponentNotificationService {
    public refreshThemeCalculations$ = new BehaviorSubject<GsTheme>('light');
}