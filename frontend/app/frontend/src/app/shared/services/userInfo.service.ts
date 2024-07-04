import { inject, Injectable } from "@angular/core";
import { BehaviorSubject, share, shareReplay, tap } from "rxjs";
import { StorageProperty, StorageService } from "./storage.service";
import { ComponentNotificationService } from "@frontend/common-services";

@Injectable()
export class UserInfoService {
    private storageService: StorageService = inject(StorageService);
    private componentNotificationService: ComponentNotificationService = inject(ComponentNotificationService);

    public playerName$ = new BehaviorSubject<string | null>(null);
    public playerUid$ = new BehaviorSubject<string | null>(null);
    public useLightModeSubject = new BehaviorSubject<boolean>(this.shouldUseLightMode());

    public shouldUseLightMode$ = this.useLightModeSubject
        .pipe(
            tap(mode => this.storageService.setStorage(
                StorageProperty.CONFIG_USE_LIGHTMODE, mode ? ThemeMode.LIGHT : ThemeMode.DARK)),
            tap(mode => this.componentNotificationService.refreshThemeCalculations$.next(mode ? 'light' : 'dark')),
            shareReplay(1)
        );

    private shouldUseLightMode(): boolean {
        const mode = this.storageService.getStorage(StorageProperty.CONFIG_USE_LIGHTMODE);
        if(mode === null) {
            return true;
        }
        return mode === ThemeMode.LIGHT;
    }
}

export enum ThemeMode {
    LIGHT = 'light',
    DARK = 'dark'
}
