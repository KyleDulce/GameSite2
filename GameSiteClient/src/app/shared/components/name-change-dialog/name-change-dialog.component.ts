import { Component } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { RestApiService } from '../../services/rest-api.service';
import { ConfigurationService } from '../../services/configuration.service';

@Component({
  selector: 'gs-name-change-dialog',
  templateUrl: './name-change-dialog.component.html',
  styleUrls: ['./name-change-dialog.component.scss']
})
export class NameChangeDialogComponent {
  public nameFieldFormControl = new FormControl('', [Validators.required]);

  public nameField: string = "";
  public submit: boolean = false;

  constructor(private dialogRef: MatDialogRef<NameChangeDialogComponent>,
    private restApiService: RestApiService,
    private configurationService: ConfigurationService) {
    dialogRef.disableClose = true;
  }

  onCancel() {
    this.dialogRef.close();
  }

  onSubmit() {
    this.submit = true;
    this.nameFieldFormControl.markAllAsTouched();
    if(this.nameFieldFormControl.valid) {
      this.restApiService.postUpdateUser({
        name: this.nameField
      }).subscribe({
        next: response => {
          if(response.data.success) {
            this.configurationService.playerName = this.nameField;
          }

          this.dialogRef.close();
        }
      })
    } else {
      this.submit = false;
    }
  }
}
