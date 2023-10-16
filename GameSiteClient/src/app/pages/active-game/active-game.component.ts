import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ErrorDialogComponent } from 'src/app/shared/components/error-dialog/error-dialog.component';
import { RestError, RestStatusCode, RoomListing } from 'src/app/shared/models/rest-api.model';
import { DevModeService } from 'src/app/shared/services/dev-mode.service';
import { RestApiService } from 'src/app/shared/services/rest-api.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'gs-active-game',
  templateUrl: './active-game.component.html',
  styleUrls: ['./active-game.component.scss'],
})
export class ActiveGameComponent implements OnInit {

  private readonly ROOM_CAPACITY_WARNING_PERCENT = 0.6;
  
  public readonly tableColumns: string[] = [
    'roomId', 'roomName', 'host', 'numPlayers', 'game', 'status', 'play', 'spectate'
  ];
  public roomList: RoomListing[] = [];

  constructor(
    private restService: RestApiService,
    private devModeService: DevModeService,
    private dialog: MatDialog,
    private router: Router,
    private snackbar: MatSnackBar
  ) {}

  ngOnInit(): void {
      this.loadRooms();
  }

  public getNumPlayersClass(listing: RoomListing): string {
    const percent = listing.lobbySize / listing.maxLobbySize;
    if(percent >= 1) {
      return "mat-error-text";
    } else if(percent >= this.ROOM_CAPACITY_WARNING_PERCENT) {
      return "mat-warning-text";
    } else {
      return "mat-success-text";
    }
  }

  public getGameStatusClass(listing: RoomListing): string {
    return listing.inProgress ? 'mat-error-text' : 'mat-success-text';
  }

  public getGameStatusText(listing: RoomListing): string {
    return listing.inProgress ? 'In Progress' : 'Waiting';
  }

  public loadRooms(): void {
    this.restService.getRoomLists()
      .subscribe({
        next: result => {
          if(!result.data) {
            console.error("Failed to load rooms");
            this.snackbar.open("Failed to load rooms", "Close", {
              duration: environment.snackbarTimeMillis
            });
            return;
          }
          this.roomList = result.data;
        },
        error: (error: RestError) => {
          if(error.statusCode === RestStatusCode.UNAUTHORIZED) {
            this.devModeService.restCallOnUnauthorized();
            return;
          }
          console.error("Failed to load rooms");
          console.error(error);
        }
      })
  }

  public joinRoom(asSpectator: boolean, roomListing: RoomListing): void {
    this.restService
      .postJoinRoom({
        asSpectator: asSpectator,
        roomId: roomListing.roomId
      })
      .subscribe({
        next: response => {
          if(response.statusCode !== RestStatusCode.OK) {
            this.triggerErrorDialog("Something went wrong. Try again Later: Bad Response");
            return;
          } else if(!response.data.success) {
            this.triggerErrorDialog("Failed to join the room. Not successful");
            return;
          }

          this.router.navigate(['/play'], {
            queryParams: {
              id: roomListing.roomId
            }
          });
        }
      })
  }

  private triggerErrorDialog(errMsg: string): void {
    this.dialog.open(ErrorDialogComponent, {
      data: errMsg
    });
  }
}
