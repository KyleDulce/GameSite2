import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnDestroy, ViewChild } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { ConfigurationService } from '../../services/configuration.service';
import { DARK_MODE_HTML_ATTRIBUTE } from '../../models/system.model';
import { NativeHtmlService } from '../../services/native-html.service';
import { RestApiService } from '../../services/rest-api.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { NameChangeDialogComponent } from '../name-change-dialog/name-change-dialog.component';
import { environment } from 'src/environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'gs-main-header',
  templateUrl: './main-header.component.html',
  styleUrls: ['./main-header.component.scss'],
})
export class MainHeaderComponent implements OnDestroy {
  @ViewChild("navMenu", {static: false}) navMenu: any;

  public links: {title: string, link: string}[] = [
    {title: "Games", link: "/rooms"},
    {title: "Create Game", link: "/createroom"},
  ];

  public isDarkMode: boolean;
  private destroy = new Subject<void>();
  public mobileMode = false;

  public get playerName(): string {
    return this.configurationService.playerName ?? "No Name";
  }

  constructor(
    breakpointObserver: BreakpointObserver, 
    private configurationService: ConfigurationService,
    private nativeHtmlService: NativeHtmlService,
    private restService: RestApiService,
    private router: Router,
    private dialogHandler: MatDialog,
    private snackbar: MatSnackBar) {
    breakpointObserver
      .observe([
        Breakpoints.XSmall
      ])
      .pipe(takeUntil(this.destroy))
      .subscribe({
        next: result => {
          this.mobileMode = result.breakpoints[Breakpoints.XSmall];
        }
      });
    
    this.isDarkMode = !configurationService.useLightMode;
  }
  
  ngOnDestroy(): void {
      this.destroy.next();
      this.destroy.complete();
  }

  public handleNameChange(): void {
    this.dialogHandler.open(NameChangeDialogComponent);
  }

  public handleLogout(): void {
    this.restService.deleteAuthToken();
    this.snackbar.open("Signed out Successfully", "Close", {
      duration: environment.snackbarTimeMillis
    });
    this.router.navigate(["/login"]);
  }

  public handleThemeToggle(): void {
    this.isDarkMode = !this.isDarkMode;
    this.configurationService.useLightMode = !this.isDarkMode;
    this.nativeHtmlService.setAttributeToRoot(DARK_MODE_HTML_ATTRIBUTE, this.isDarkMode.toString());
  }
}
