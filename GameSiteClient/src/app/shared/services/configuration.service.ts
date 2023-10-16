import { Injectable } from '@angular/core';
import { StorageProperty, StorageService } from './storage.service';
import { NativeHtmlService } from './native-html.service';
import { ConfigOptions } from '../models/configuration.model';
import { Observable, Subject } from 'rxjs';

@Injectable()
export class ConfigurationService {
  private static readonly GAME_AUTH_COOKIE = "Game-AuthCookie";

  private config: ConfigOptions;
  private previousUrl: string = "/";
  private _loggedIn: boolean = false;
  private loggedInSubject: Subject<boolean> = new Subject();

  constructor(private storageService: StorageService, private nativeHtmlService: NativeHtmlService) {
    let lightMode: string | null = storageService.getStorage(StorageProperty.CONFIG_USE_LIGHTMODE);
    if(lightMode === null) {
      lightMode = 'true';
    }
    const lightModeResult = lightMode === 'true' ? true : false;

    this.config = {
      PlayerName: "No Name",
      Uid: null,
      UseLightMode: lightModeResult,
    }
  }

  public containsAuthCookie(): boolean {
    return this.nativeHtmlService.containsCookie(ConfigurationService.GAME_AUTH_COOKIE);
  }

  public getAuthCookie(): string | null {
    return this.nativeHtmlService.getCookieValue(ConfigurationService.GAME_AUTH_COOKIE);
  }

  public get playerName(): string | null {
    return this.config.PlayerName;
  }

  public get uid(): string | null {
    return this.config.Uid;
  }

  public get useLightMode(): boolean {
    return this.config.UseLightMode;
  }

  public get getPrevUrl(): string {
    return this.previousUrl;
  }

  public set playerName(playerName: string | null) {
    this.config.PlayerName = playerName;
  }

  public set uid(uid: string | null) {
    this.config.Uid = uid;
  }

  public set useLightMode(useLightMode: boolean) {
    this.config.UseLightMode = useLightMode;
    this.storageService.setStorage(StorageProperty.CONFIG_USE_LIGHTMODE, useLightMode.toString());
  }

  public set setPrevUrl(value: string) {
    this.previousUrl = value;
  }

  public get loggedIn(): boolean {
    return this._loggedIn;
  }

  public set loggedIn(value: boolean) {
    this._loggedIn = value;
    this.loggedInSubject.next(value);
  }

  public get loggedInObservable(): Observable<boolean> {
    return this.loggedInSubject.asObservable();
  }
}
