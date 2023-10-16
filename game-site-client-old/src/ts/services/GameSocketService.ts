import { Observable, Subject, filter, map, tap } from "rxjs";
import SocketService from "./SocketService"
import { ChatMessageData, CommonGameTypeStrings, GameDataMessage, GameDataUpdate, InvalidSocketMessage, isCompliantGameDataMessage, isCompliantInvalidSocketMessage } from "../model/SocketModel";
import { getAuthCookie } from "../model/ConfigOptions";

const SocketListen = "/socket/queue/gamePlayer"; // private
const SocketTopic = "/socket/topic/game" // broadcasted
const SocketSend = "/socket/app/game" 

export default class GameSocketService {

    private roomId: string;
    private socketService: SocketService

    private allMessageSubject: Subject<GameDataMessage | InvalidSocketMessage>;
    private chatMessageSubject: Subject<ChatMessageData>;
    private broadcastSubject: Subject<GameDataMessage>;
    private privateSubject: Subject<GameDataMessage>;
    private unauthorizedCallSubject: Subject<void>;

    protected constructor(roomId: string, socketService: SocketService) {
        this.roomId = roomId;
        this.socketService = socketService;

        this.allMessageSubject = new Subject<GameDataMessage | InvalidSocketMessage>();
        this.chatMessageSubject = new Subject<ChatMessageData>();
        this.broadcastSubject = new Subject<GameDataMessage>();
        this.privateSubject = new Subject<GameDataMessage>();
        this.unauthorizedCallSubject = new Subject<void>();

        this.setupListenWatchEndpoint();
        this.setupPrivateWatchEndpoint();
    }

    private setupListenWatchEndpoint() {
        this.listenAndFilterEndpoint(SocketTopic + this.roomId)
        .pipe(
            tap(message => {
                if(message.gameDataIdString === CommonGameTypeStrings.CHAT_MESSAGE_DATA) {
                    this.chatMessageSubject.next(message.data as ChatMessageData);
                }
            }),
            tap(message => {
                this.allMessageSubject.next(message);
                this.broadcastSubject.next(message);
            })
        ).subscribe();
    }

    private setupPrivateWatchEndpoint() {
        this.listenAndFilterEndpoint(SocketListen)
        .pipe(
            tap(message => {
                this.allMessageSubject.next(message);
                this.privateSubject.next(message);
            })
        )
    }

    private listenAndFilterEndpoint(endpoint: string): Observable<GameDataMessage> {
        return this.socketService.watchEndpoint(endpoint)
        .pipe(
            filter(message => {
                if(isCompliantInvalidSocketMessage(message.body)) {
                    console.error(message.body);
                    if((message.body as InvalidSocketMessage).code === 401) {
                        this.unauthorizedCallSubject.next();
                        this.allMessageSubject.next(message.body as InvalidSocketMessage);
                    }
                    return false;
                }
                return isCompliantGameDataMessage(message.body)
            }),
            map(message => message.body as GameDataMessage),
            filter(message => message.roomId === this.roomId)
        );
    }

    public async disconnect() {
        this.allMessageSubject.complete();
        this.chatMessageSubject.complete();
        this.broadcastSubject.complete();
        this.privateSubject.complete();
        this.unauthorizedCallSubject.complete();
    }

    public sendGameMessage(gameDataIdString: string, data: any) {
        const request: GameDataUpdate = {
            authToken: getAuthCookie() as string,
            gameData: {
                roomId: this.roomId,
                gameDataIdString: gameDataIdString,
                data: data
            }
        }
        this.socketService.sendMessage(SocketSend, request);
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

    static createGameConnection(roomId: string, socketService: SocketService): GameSocketService {
        try {
            return new GameSocketService(roomId, socketService);
        } catch(e) {
            throw new Error("Cannot connect to socket endpoint");
        }
    }
}