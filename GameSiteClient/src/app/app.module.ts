import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { MainHeaderComponent } from './shared/components/main-header/main-header.component';
import { ErrorDialogComponent } from './shared/components/error-dialog/error-dialog.component';
import { NameChangeDialogComponent } from './shared/components/name-change-dialog/name-change-dialog.component';
import { JoinSelectDialogComponent } from './shared/components/join-select-dialog/join-select-dialog.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';
import { CreateGameComponent } from './pages/create-game/create-game.component';
import { ActiveGameComponent } from './pages/active-game/active-game.component';
import { PlayGameComponent } from './pages/play-game/play-game.component';
import { ChatMenuComponent } from './pages/play-game/chat-menu/chat-menu.component';

import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { StorageService } from './shared/services/storage.service';
import { NativeHtmlService } from './shared/services/native-html.service';
import { DevModeService } from './shared/services/dev-mode.service';
import { ConfigurationService } from './shared/services/configuration.service';
import { SettingsMenuComponent } from './pages/play-game/player-settings-menu/player-settings-menu.component';

@NgModule({
  declarations: [
    AppComponent,
    MainHeaderComponent,
    ErrorDialogComponent,
    NameChangeDialogComponent,
    JoinSelectDialogComponent,
    HomeComponent,
    PlayGameComponent,
    CreateGameComponent,
    ActiveGameComponent,
    LoginComponent,
    NotFoundComponent,
    ChatMenuComponent,
    SettingsMenuComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  providers: [
    StorageService,
    NativeHtmlService,
    DevModeService,
    ConfigurationService,
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
