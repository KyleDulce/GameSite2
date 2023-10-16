import { Component, OnInit } from "@angular/core";
import { filter, tap } from "rxjs";
import { User } from "src/app/shared/models/rest-api.model";
import { CommonGameTypeStrings, KickPlayerData, SettingsDataResponse } from "src/app/shared/models/socket.model";
import { ConfigurationService } from "src/app/shared/services/configuration.service";
import { GameSocketService } from "src/app/shared/services/game-socket.service";

@Component({
    selector: 'gs-player-settings-menu',
    templateUrl: './player-settings-menu.component.html',
    styleUrls: ['./player-settings-menu.component.scss'],
  })
export class SettingsMenuComponent implements OnInit {
  private players: Array<User> = [];
  private hostUid: string | null = null;

  constructor(private configurationService: ConfigurationService, private gameSocketService: GameSocketService) {}

  ngOnInit(): void {
    this.hostUid = this.configurationService.uid;
    this.gameSocketService.privateMessages
      .pipe(
        filter(message => message.gameDataIdString === CommonGameTypeStrings.SETTINGS_DATA_RESPONSE),
        tap(message => {
          this.players = (message.data as SettingsDataResponse).players;
        })
      ).subscribe();
    this.gameSocketService.sendGameMessage(CommonGameTypeStrings.SETTINGS_DATA_REQUEST, null);
  }

  public get nonHostPlayers(): Array<User> {
    return this.players.filter(p => p.uuid !== this.hostUid);
  }

  public get emptyPlayers(): boolean {
    return this.nonHostPlayers.length <= 0;
  }

  public onPlayerKick(player: User): void {
    const kickPlayerData: KickPlayerData = {
      player: player
    };
    this.gameSocketService.sendGameMessage(CommonGameTypeStrings.KICK_PLAYER_DATA, kickPlayerData)
  }
}
