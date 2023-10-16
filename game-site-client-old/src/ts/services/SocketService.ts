import { RxStomp, RxStompConfig } from "@stomp/rx-stomp";
import { Observable, Subject, map, takeUntil } from "rxjs";
import SockJS from "sockjs-client";
import { ISocketMessage } from "../model/SocketModel";

export default class SocketService {
    private rxStomp: RxStomp;
    private unSubscribeSubject: Subject<void>;

    protected constructor() {
        this.rxStomp = new RxStomp();
        const rxStompConfig: RxStompConfig = {
            webSocketFactory: () =>
                new SockJS(
                    (process.env.REACT_APP_BACKEND_URL ||
                        "http://localhost:8080") +
                        (process.env.REACT_APP_STOMP_ENDPOINT || "")
                ),
        };
        this.rxStomp.configure(rxStompConfig);
        this.unSubscribeSubject = new Subject();
        this.rxStomp.activate();
    }

    public watchEndpoint(endpoint: string): Observable<ISocketMessage> {
        return this.rxStomp
            .watch({
                destination: endpoint,
            })
            .pipe(
                takeUntil(this.unSubscribeSubject),
                map((message) => {
                    return {
                        body: JSON.parse(message.body),
                    };
                })
            );
    }

    public sendMessage(endpoint: string, message: any) {
        this.rxStomp.publish({
            destination: endpoint,
            body: JSON.stringify(message),
        });
    }

    public async disconnect() {
        //cleanup
        //prevents memory leaks
        this.unSubscribeSubject.next();
        this.unSubscribeSubject.complete();

        //close connection
        this.rxStomp.deactivate();
    }

    static createConnection(): SocketService {
        try {
            return new SocketService();
        } catch (e) {
            throw new Error("Cannot connect to socket endpoint");
        }
    }
}
