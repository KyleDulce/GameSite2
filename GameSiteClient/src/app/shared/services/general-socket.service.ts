import { Injectable } from '@angular/core';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { Observable, Subject, first, map, takeUntil } from 'rxjs';
import SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';
import { ISocketMessage } from '../models/socket.model';

@Injectable({
  providedIn: 'root',
})
export class GeneralSocketService {
  private rxStomp: RxStomp;
  private unSubscribeSubject: Subject<void>;
  private clientCount: number = 0;

  public constructor() {
    this.rxStomp = new RxStomp();
    this.unSubscribeSubject = new Subject();
  }

  public get connected(): boolean {
    return this.rxStomp.connected();
  }

  public get onConnect(): Observable<void> {
    return this.rxStomp
      .connected$
      .pipe(
        first(),
        map(() => {})
      );
  }

  public connect() {
    const rxStompConfig: RxStompConfig = {
      webSocketFactory: () =>
        new SockJS(environment.backendUrl + environment.stompEndpoint),
    };
    this.rxStomp.configure(rxStompConfig);

    this.rxStomp.activate();
  }

  public addClientConnection(): void {
    if (!this.rxStomp.active) {
      this.connect();
    }
    this.clientCount++;
  }

  public removeClientConnection(): void {
    if (this.clientCount <= 0 || !this.rxStomp.active) {
      return;
    }
    this.clientCount--;
    if(this.clientCount <= 0) {
      this.disconnect();
    }
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
    this.unSubscribeSubject = new Subject();

    //close connection
    this.rxStomp.deactivate();
  }
}
