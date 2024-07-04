import { NgModule } from '@angular/core';
import { ApiModule, BASE_PATH } from '@frontend/api-sdk';
import { environment } from '../environments/environment';
import { provideRouter, RouterModule } from '@angular/router';
import { MaterialModule } from './material.module';
import { MainHeaderComponent } from './components/main-header/main-header.component';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent } from './app.component';
import { HomeComponent } from './pages/home/home.component';
import { appRoutes } from './app.routes';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { UserInfoService } from './shared/services/userInfo.service';
import { ComponentNotificationService } from '@frontend/common-services';
import { StorageService } from './shared/services/storage.service';
import { ElementsModule } from '@frontend/elements';
import { ComponentsModule } from '@frontend/components';
import { LayoutModule } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './pages/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    MainHeaderComponent,
    HomeComponent,
    LoginComponent,
  ],
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule,
    MaterialModule,
    ApiModule,
    BrowserAnimationsModule,
    ComponentsModule,
    ElementsModule,
    LayoutModule,
    ReactiveFormsModule,
    FormsModule
  ],
  providers: [
    ComponentNotificationService,
    UserInfoService,
    StorageService,
    Document,
    provideRouter(appRoutes),
    provideHttpClient(),
    { provide: BASE_PATH, useValue: environment.backendUrl },
  ],
  bootstrap: [AppComponent],
  exports: [LoginComponent, LoginComponent],
})
export class AppModule {}
