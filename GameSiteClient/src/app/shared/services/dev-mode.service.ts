import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { RestApiService } from './rest-api.service';
import { environment } from 'src/environments/environment';
import { ConfigurationService } from './configuration.service';
import { Observable } from 'rxjs';
import { RestMessage, UserAuthResponse } from '../models/rest-api.model';
import { SHA256 } from 'crypto-js';
import { Location } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable()
export class DevModeService {

    readonly HOME_PATH = "/";
    readonly LOGIN_PATH = "/login"

    constructor(
        private location: Location,
        private router: Router,
        private restApiService: RestApiService,
        private configurationService: ConfigurationService,
        private snackbar: MatSnackBar
    ) {}

    public appPageLayoutShouldRedirect(): void {
        const containsAuth = this.configurationService.containsAuthCookie();
        const routePath = this.location.path();
        if(environment.production) {
            if(routePath !== this.LOGIN_PATH && !containsAuth) {
                this.snackbar.open("Please sign in", "Close", {
                    duration: environment.snackbarTimeMillis
                });
                this.router.navigate([this.LOGIN_PATH], {replaceUrl: false});
            } else if(routePath === this.LOGIN_PATH && containsAuth) {
                this.router.navigate([ this.HOME_PATH ], {replaceUrl: true});
            }
        } else {
            if(routePath !== this.LOGIN_PATH && 
                !environment.ignoreAuth &&
                !containsAuth) {
                    if(environment.autoLogin) {
                        this.devModeAutoLogin()
                        ?.subscribe({
                            error: err => {
                                console.error(err);
                                this.router.navigate([this.LOGIN_PATH]);
                            }
                        });
                    } else {
                        console.log("nav");
                        this.router.navigate([this.LOGIN_PATH], {replaceUrl: false});
                    }
            } else if((routePath === this.LOGIN_PATH &&
                containsAuth) ||
                environment.ignoreAuth) {
                    console.log("Credentials Available. Going Home path")
                    this.router.navigate([ this.HOME_PATH ], {replaceUrl: true});
            }
        }
    }

    public restCallOnUnauthorized(): void {
        this.configurationService.loggedIn = false;
        if(environment.production) {
            this.snackbar.open("You have timed out. Please sign in again", "Close", {
                duration: environment.snackbarTimeMillis
            });
            this.router.navigate([this.LOGIN_PATH]);
        } else {
            if(!environment.ignoreAuth && environment.autoLogin) {
                this.devModeAutoLogin()?.subscribe({
                    next: () => {
                        const currentUrl = this.router.url;
                        this.configurationService.loggedIn = true;
                        this.router.navigateByUrl('/', {skipLocationChange: true})
                        .then(() => {
                            this.router.navigateByUrl(currentUrl)
                        });
                    }
                });
            }
        }
    }

    private devModeAutoLogin(): Observable<RestMessage<UserAuthResponse>> | undefined {
        if(!environment.production) {
            this.snackbar.open("Auto logging in via dev credentials in environment", "Close", {
                duration: environment.snackbarTimeMillis
            });
            console.log("Auto logging in via dev credentials in environment");
            const loginCreds = (environment.autoLogin as string | null)?.split(",");

            if(!loginCreds) {
                console.error("Credentials not available for dev mode!");
                return;
            }

            const hashedPassword = SHA256(loginCreds[1]).toString();
            return this.restApiService.postAuth({
                login: loginCreds[0],
                passHash: hashedPassword
            });
        }
        return;
    }
}
