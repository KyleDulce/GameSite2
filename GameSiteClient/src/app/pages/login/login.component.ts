import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ErrorDialogComponent } from 'src/app/shared/components/error-dialog/error-dialog.component';
import { RestError, RestStatusCode } from 'src/app/shared/models/rest-api.model';
import { ConfigurationService } from 'src/app/shared/services/configuration.service';
import { RestApiService } from 'src/app/shared/services/rest-api.service';

@Component({
  selector: 'gs-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  public loginFormGroup: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required])
  });
  public showPassword: boolean = false;
  public isLoading: boolean = false;

  constructor(
    private restApiService: RestApiService,
    private configurationService: ConfigurationService,
    private router: Router,
    private matDialog: MatDialog
    ) {}

  public togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  public onSubmit() {
    if(!this.loginFormGroup.valid) {
      this.loginFormGroup.markAllAsTouched();
      return;
    }

    this.isLoading = true;

    this.restApiService.postAuth({
      login: this.loginFormGroup.controls['username'].value,
      passHash: this.loginFormGroup.controls['password'].value
    }).subscribe({
      next: response => {
        if(response.data.success === true && response.statusCode === RestStatusCode.OK) {
          this.configurationService.loggedIn = true;
          if(!this.configurationService.getPrevUrl.startsWith('/play?')) {
            this.router.navigateByUrl(this.configurationService.getPrevUrl);
          } else {
            this.router.navigateByUrl('/rooms');
          }
        } else {
          this.isLoading = false;
          this.matDialog.open(ErrorDialogComponent, {
            data: 'Something went wrong. Try again later'
          });
        }
      },
      error: (error: RestError) => {
        if(error.statusCode === RestStatusCode.UNAUTHORIZED) {
          this.loginFormGroup.setErrors({
            invalidPassword: true
          });
        } else {
          this.matDialog.open(ErrorDialogComponent, {
            data: 'Something went wrong. Try again later'
          });
        }
        this.isLoading = false;
      }
    })
  }
}
