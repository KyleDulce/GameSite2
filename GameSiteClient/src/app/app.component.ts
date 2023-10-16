import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router, RoutesRecognized } from '@angular/router';
import { Observable, Subscription, filter, interval, map, pairwise, tap } from 'rxjs';
import { NativeHtmlService } from './shared/services/native-html.service';
import { DARK_MODE_HTML_ATTRIBUTE } from './shared/models/system.model';
import { ConfigurationService } from './shared/services/configuration.service';
import { DevModeService } from './shared/services/dev-mode.service';
import { routes } from './app-routing.module';
import { environment } from 'src/environments/environment';
import { RestApiService } from './shared/services/rest-api.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit {
  showHeader: boolean = true;

  private refreshInterval?: Observable<number>;
  private refreshSubscription?: Subscription;

  constructor(
    router: Router, 
    private nativeHtmlService: NativeHtmlService, 
    private configurationService: ConfigurationService,
    private devModeService: DevModeService,
    private restService: RestApiService) {
    router.events.pipe(
      filter((value) => value instanceof NavigationEnd),
      map((value) => value as NavigationEnd),
      tap((value) => {
        let isValidUrl = false;
        routes.forEach(route => {
          const extractedLocation = value.url.match(/^[^?]+/);
          if(extractedLocation != null && extractedLocation[0] === `/${route.path}`) {
            isValidUrl = true;  
          }
        });

        this.showHeader = isValidUrl && value.url !== '/login';
      })
    ).subscribe();
    router.events.pipe(
      filter((value) => value instanceof RoutesRecognized),
      map((value) => value as RoutesRecognized),
      pairwise(),
      tap((value) => {
        this.configurationService.setPrevUrl = value[0].urlAfterRedirects;
      })
    ).subscribe();
  }

  ngOnInit(): void {
    this.nativeHtmlService.setAttributeToRoot(DARK_MODE_HTML_ATTRIBUTE, (!this.configurationService.useLightMode).toString());

    this.configurationService.loggedInObservable
    .pipe(
      tap(value => this.onLoggedInChange(value))
    ).subscribe();

    if(this.configurationService.containsAuthCookie()) {
      this.restService.getRefreshToken()
      .subscribe(value => {
        if(value) {
          this.configurationService.loggedIn = true;
        } else {
          this.devModeService.restCallOnUnauthorized();
        }
      })
    } 
    this.devModeService.appPageLayoutShouldRedirect();
  }

  private onLoggedInChange(value: boolean): void {
    if(value && !this.refreshSubscription) {
      this.refreshInterval = interval(environment.gameRefreshTokenIntervalSeconds * 1000);
      this.refreshSubscription = this.refreshInterval
        .pipe(
          tap(() => {
            this.restService.getRefreshToken();
          })
        ).subscribe();
    } else if(!value && this.refreshSubscription) {
        this.refreshSubscription.unsubscribe();
        this.refreshSubscription = undefined;
        this.refreshInterval = undefined;
    }
  }
}
