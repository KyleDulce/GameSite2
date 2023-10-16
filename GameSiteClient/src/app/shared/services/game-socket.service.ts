import { Injectable } from '@angular/core';
import { GeneralSocketService } from './general-socket.service';
import {
  ChatMessageData,
  CommonGameTypeStrings,
  GameDataMessage,
  GameDataUpdate,
  InvalidSocketMessage,
  isCompliantGameDataMessage,
  isCompliantInvalidSocketMessage,
} from '../models/socket.model';
import { Observable, Subject, filter, map, tap } from 'rxjs';
import { ConfigurationService } from './configuration.service';

@Injectable({
  providedIn: 'root',
})
export class GameSocketService {
  private static readonly SocketListen = '/socket/user/queue/gamePlayer'; // private
  private static readonly SocketTopic = '/socket/topic/game'; // broadcasted
  private static readonly SocketSend = '/socket/app/game';

  private roomId: string = '';

  private allMessageSubject: Subject<GameDataMessage | InvalidSocketMessage>;
  private chatMessageSubject: Subject<ChatMessageData>;
  private broadcastSubject: Subject<GameDataMessage>;
  private privateSubject: Subject<GameDataMessage>;
  private unauthorizedCallSubject: Subject<void>;

  public constructor(
    private socketService: GeneralSocketService,
    private configurationService: ConfigurationService
  ) {
    this.allMessageSubject = new Subject<
      GameDataMessage | InvalidSocketMessage
    >();
    this.chatMessageSubject = new Subject<ChatMessageData>();
    this.broadcastSubject = new Subject<GameDataMessage>();
    this.privateSubject = new Subject<GameDataMessage>();
    this.unauthorizedCallSubject = new Subject<void>();
  }

  public connect(roomId: string): void {
    this.roomId = roomId;
    this.socketService.addClientConnection();

    this.setupListenWatchEndpoint();
    this.setupPrivateWatchEndpoint();
  }

  public get onConnect(): Observable<void> {
    return this.socketService.onConnect;
  }

  private setupListenWatchEndpoint() {
    this.listenAndFilterEndpoint(GameSocketService.SocketTopic + this.roomId)
      .pipe(
        tap((message) => {
          if (
            message.gameDataIdString === CommonGameTypeStrings.CHAT_MESSAGE_DATA
          ) {
            this.chatMessageSubject.next(message.data as ChatMessageData);
          }
        }),
        tap((message) => {
          this.allMessageSubject.next(message);
          this.broadcastSubject.next(message);
        })
      )
      .subscribe();
  }

  private setupPrivateWatchEndpoint() {
    this.listenAndFilterEndpoint(GameSocketService.SocketListen).pipe(
      tap((message) => {
        this.allMessageSubject.next(message);
        this.privateSubject.next(message);
      })
    ).subscribe();
  }

  private listenAndFilterEndpoint(
    endpoint: string
  ): Observable<GameDataMessage> {
    return this.socketService.watchEndpoint(endpoint).pipe(
      filter((message) => {
        if (isCompliantInvalidSocketMessage(message.body)) {
          console.error(message.body);
          if ((message.body as InvalidSocketMessage).code === 401) {
            this.unauthorizedCallSubject.next();
            this.allMessageSubject.next(message.body as InvalidSocketMessage);
          }
          return false;
        }
        return isCompliantGameDataMessage(message.body);
      }),
      map((message) => message.body as GameDataMessage),
      filter((message) => message.roomId === this.roomId)
    );
  }

  public async disconnect() {
    this.socketService.removeClientConnection();

    this.allMessageSubject.complete();
    this.chatMessageSubject.complete();
    this.broadcastSubject.complete();
    this.privateSubject.complete();
    this.unauthorizedCallSubject.complete();

    this.allMessageSubject = new Subject<
      GameDataMessage | InvalidSocketMessage
    >();
    this.chatMessageSubject = new Subject<ChatMessageData>();
    this.broadcastSubject = new Subject<GameDataMessage>();
    this.privateSubject = new Subject<GameDataMessage>();
    this.unauthorizedCallSubject = new Subject<void>();
  }

  public sendGameMessage(gameDataIdString: string, data: any) {
    const request: GameDataUpdate = {
      authToken: this.configurationService.getAuthCookie() as string,
      gameData: {
        roomId: this.roomId,
        gameDataIdString: gameDataIdString,
        data: data,
      },
    };
    this.socketService.sendMessage(GameSocketService.SocketSend, request);
  }

  public get allMessages() {
    return this.allMessageSubject.asObservable();
  }

  public get chatMessages() {
    return this.chatMessageSubject.asObservable();
  }

  public get broadcastMessages() {
    return this.broadcastSubject.asObservable();
  }

  public get privateMessages() {
    return this.privateSubject.asObservable();
  }

  public get unauthorizedMessages() {
    return this.unauthorizedCallSubject.asObservable();
  }
}
