import {
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { GameLoadingService } from './game-loading.service';
import { GameSocketService } from 'src/app/shared/services/game-socket.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RestApiService } from 'src/app/shared/services/rest-api.service';
import {
  RestMessage,
  RestStatusCode,
  RoomInfoResponse,
} from 'src/app/shared/models/rest-api.model';
import {
  isValidGameTypeNum,
  parseNumberToGameType,
} from 'src/app/shared/models/system.model';
import { concatMap, filter, tap } from 'rxjs';
import { ComponentPortal, Portal } from '@angular/cdk/portal';
import { CommonGameTypeStrings } from 'src/app/shared/models/socket.model';
import { DevModeService } from 'src/app/shared/services/dev-mode.service';
import { environment } from 'src/environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'gs-play-game',
  templateUrl: './play-game.component.html',
  styleUrls: ['./play-game.component.scss'],
  providers: [GameLoadingService],
  host: {
    class: 'gs-flex-component',
  },
})
export class PlayGameComponent implements OnInit, OnDestroy {
  private roomId: string = "";

  public gameOutlet?: Portal<any>;
  public settingOutlet?: Portal<any>;
  public isHost: boolean = false;
  public gameLoaded: boolean = false;
  public isChatSelected: boolean = false;
  public isSidebarOpen: boolean = false;
  public asPlayer: boolean = false;

  constructor(
    private restApiService: RestApiService,
    private activatedRoute: ActivatedRoute,
    private gameSocketService: GameSocketService,
    private devModeService: DevModeService,
    private router: Router,
    private gameLoadingService: GameLoadingService,
    private snackbar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const idQueryParam = this.activatedRoute.snapshot.queryParams['id'];

    if (idQueryParam) {
      this.roomId = idQueryParam;
      this.restApiService.getRoomInfo(idQueryParam).subscribe({
        next: (response) => this.onGetRoomInfoSuccess(response),
        error: (error) => {
          this.navigateRoomsOnError();
        },
      });
    } else {
      this.navigateRoomsOnError();
      this.roomId = '';
    }
  }

  ngOnDestroy(): void {
    this.gameSocketService.disconnect();
  }

  private onGetRoomInfoSuccess(response: RestMessage<RoomInfoResponse>): void {
    if (response.statusCode !== RestStatusCode.OK) {
      this.navigateRoomsOnError();
      return;
    }

    const responseData = response.data;

    if (!responseData.isSpectating && !responseData.joinedRoom) {
      this.navigateRoomsOnError();
      return;
    }

    this.asPlayer = responseData.joinedRoom;
    this.isHost = responseData.isHost;

    if (!isValidGameTypeNum(responseData.room.gameType)) {
      this.navigateRoomsOnError();
      return;
    }

    const gameType = parseNumberToGameType(responseData.room.gameType);

    this.gameLoadingService
      .loadGameModule(gameType)
      .pipe(
        tap(loadedTypes => {
          this.gameOutlet = new ComponentPortal(loadedTypes.game);
          this.settingOutlet = new ComponentPortal(loadedTypes.settings);
        }),
        concatMap(() => {
          const connectListener = this.gameSocketService.onConnect;
          this.gameSocketService.connect(this.roomId);
          return connectListener;
        }),
        tap(() => {
          this.onGameLoaded();
        })
      )
      .subscribe({
        error: (err) => {
          this.navigateRoomsOnError();
        },
      });
  }

  private onGameLoaded(): void {
    this.gameSocketService.unauthorizedMessages
      .subscribe(() => this.onUnauthorizedCall());

    this.gameSocketService.privateMessages
      .pipe(
        filter(message => {
          return message.gameDataIdString === CommonGameTypeStrings.FORCE_KICK_DATA}),
        tap(message => this.onKicked())
      ).subscribe();

    this.gameLoaded = true;
  }

  private navigateRoomsOnError(): void {
    this.snackbar.open("Something went wrong", "Close", {
      duration: environment.snackbarTimeMillis
    });
    this.router.navigate(['/rooms']);
  }

  private onUnauthorizedCall(): void {
    this.devModeService.restCallOnUnauthorized();
  }

  private onKicked(): void {
    this.snackbar.open("You were kicked from the room", "Close", {
      duration: environment.snackbarTimeMillis
  });
    this.router.navigate(['/rooms']);
  }

  public toggleDrawer(chatToggled: boolean): void {
    if (this.isSidebarOpen) {
      if (chatToggled === this.isChatSelected) {
        this.isSidebarOpen = false;
      } else {
        this.isChatSelected = !this.isChatSelected;
      }
    } else {
      this.isChatSelected = chatToggled;
      this.isSidebarOpen = true;
    }
  }

  public closeDrawer(): void {
    this.isSidebarOpen = false;
  }
}
