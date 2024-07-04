import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, map, switchMap } from 'rxjs';
import { UserInfoService } from './shared/services/userInfo.service';

@Component({
  selector: 'gs-app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'GameSite';

  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  private userInfoService: UserInfoService = inject(UserInfoService);
  private router: Router = inject(Router);

  protected hideHeader = toSignal(
    this.router.events.pipe(
      filter((value): value is NavigationEnd => value instanceof NavigationEnd),
      switchMap(() => this.activatedRoute.firstChild ? this.activatedRoute.firstChild.data : this.activatedRoute.data),
      map(data => data['hideHeader'])
    )
  );

  protected themeClass = toSignal(
    this.userInfoService.shouldUseLightMode$.pipe(
      map(shouldUseLightMode => `${shouldUseLightMode ? 'light' : 'dark'}-theme`)
    )
  );
}
