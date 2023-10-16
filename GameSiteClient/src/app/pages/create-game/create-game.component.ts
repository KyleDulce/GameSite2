import { Component } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldControl } from '@angular/material/form-field';
import { Router } from '@angular/router';
import { ErrorDialogComponent } from 'src/app/shared/components/error-dialog/error-dialog.component';
import { RestError, RestStatusCode } from 'src/app/shared/models/rest-api.model';
import { GameType, validGameTypes } from 'src/app/shared/models/system.model';
import { ConfigurationService } from 'src/app/shared/services/configuration.service';
import { DevModeService } from 'src/app/shared/services/dev-mode.service';
import { RestApiService } from 'src/app/shared/services/rest-api.service';

@Component({
  selector: 'gs-create-game',
  templateUrl: './create-game.component.html',
  styleUrls: ['./create-game.component.scss'],
})
export class CreateGameComponent {
  public roomNameValue: string = "";
  public gameTypeValue: GameType | null = null;
  public maxPlayersValue: number = 10;
  public isRequested: boolean = false;

  public createRoomForm = new FormGroup({
    roomNameFieldControl: new FormControl('', [Validators.required]),
    gameTypeFieldControl: new FormControl<GameType | null>(null, [Validators.required]),
    maxPlayersFieldControl: new FormControl(10, [Validators.required, Validators.min(1)])
  })

  public get listedGameTypes(): GameType[] {
    return validGameTypes;
  }

  constructor(
    private restApiService: RestApiService, 
    private configuration: ConfigurationService, 
    private dialog: MatDialog,
    private router: Router,
    private devModeService: DevModeService) {}

  public onSubmit(): void {
    this.createRoomForm.markAllAsTouched();
    if(!this.createRoomForm.valid) {
      return;
    }
    this.isRequested = true;
    this.restApiService.postCreateRoom({
      host: {
        uuid: this.configuration.uid as string,
        name: this.configuration.playerName as string,
        isGuest: false
      },
      gameType: this.createRoomForm.get(['gameTypeFieldControl'])?.value,
      maxLobbySize: this.createRoomForm.get(['maxPlayersFieldControl'])?.value
    }).subscribe({
      next: response => {
        if(response.statusCode !== RestStatusCode.OK || !response.data.success) {
          this.openErrorDialog();
          return;
        }
        this.router.navigate(['/play'], {
          queryParams: {
            id: response.data.roomId
          },
          state: {
            asPlayer: true,
            asHost: true
          }
        })
      },
      error: (error: RestError) => {
        if(error.statusCode === RestStatusCode.UNAUTHORIZED) {
          this.devModeService.restCallOnUnauthorized();
          return;
        }
        this.openErrorDialog();
        console.error(error);
        this.isRequested = false;
      }
    });
  }

  private openErrorDialog(): void {
    this.dialog.open(ErrorDialogComponent, {
      data: 'Something went wrong. Try again later'
    });
  }
}
