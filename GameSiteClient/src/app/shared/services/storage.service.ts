import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

export enum StorageProperty {
  CONFIG_USE_LIGHTMODE = 'gs-config-use-light',
}

@Injectable()
export class StorageService {
  public getStorage(storageProperty: StorageProperty): string | null {
    if (environment.useSessionStorage) {
      return sessionStorage.getItem(storageProperty);
    } else {
      return localStorage.getItem(storageProperty);
    }
  }

  public setStorage(
    storageProperty: StorageProperty,
    value: string | undefined
  ): void {
    if (value) {
      this.saveStorage(storageProperty, value);
    } else {
      this.removeStorage(storageProperty);
    }
  }

  private saveStorage(key: string, value: string): void {
    if (environment.useSessionStorage) {
      sessionStorage.setItem(key, value);
    } else {
      localStorage.setItem(key, value);
    }
  }

  private removeStorage(key: string): void {
    if (environment.useSessionStorage) {
      sessionStorage.removeItem(key);
    } else {
      localStorage.removeItem(key);
    }
  }

  private clearStorage(): void {
    if (environment.useSessionStorage) {
      sessionStorage.clear();
    } else {
      localStorage.clear();
    }
  }
}
