import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, computed, inject, ViewChild } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UserInfoService } from '../../shared/services/userInfo.service';
import { map } from 'rxjs';
import { MenuItem } from '@frontend/components';
import { Router } from '@angular/router';

type LinkRecord = Record<string, {text: string, link: string}>;

@Component({
  selector: 'gs-main-header',
  templateUrl: './main-header.component.html',
  styleUrls: ['./main-header.component.scss'],
})
export class MainHeaderComponent {
  private breakpointObserver: BreakpointObserver = inject(BreakpointObserver);
  private userInfoService: UserInfoService = inject(UserInfoService);
  private router: Router = inject(Router);

  @ViewChild("navMenu", {static: false}) navMenu: any;
  public playerName = toSignal(this.userInfoService.playerName$.pipe(
    map(name => name ?? "No Name")
  ));
  public isLightMode = toSignal(this.userInfoService.shouldUseLightMode$);
  public isMobileMode = toSignal(
    this.breakpointObserver
      .observe([
        Breakpoints.XSmall
      ]).pipe(
        map(state => !state.matches)
      )
    );

  public readonly availableLinks: LinkRecord = {
    home: {
      text: "GameSite",
      link: "/"
    },
    game: {
      text: "Games",
      link: "/rooms"
    },
    create: {
      text: "Create Game",
      link: "/createroom"
    }
  };

  public menuLinks = computed(() => {
    const keys = Object.keys(this.availableLinks) as Array<keyof LinkRecord>;
    return keys.map(key => {
      const route = this.availableLinks[key];
      return {
        menuItemId: key,
        text: route.text
      } satisfies MenuItem;
    });
  });

  public handleRoute(id: string) {
    const link = this.availableLinks[id].link;
    this.router.navigate([link]);
  }

  public handleLogout(): void {
    // this.restService.deleteAuthToken();
    // this.snackbar.open("Signed out Successfully", "Close", {
    //   duration: environment.snackbarTimeMillis
    // });
    // this.router.navigate(["/login"]);
  }

  public handleThemeToggle(): void {
    // this.isDarkMode = !this.isDarkMode;
    // this.configurationService.useLightMode = !this.isDarkMode;
    // this.nativeHtmlService.setAttributeToRoot(DARK_MODE_HTML_ATTRIBUTE, this.isDarkMode.toString());
    this.userInfoService.useLightModeSubject.next(!this.isLightMode());
  }
}
