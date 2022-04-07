import { Injectable } from '@angular/core';
import { Client, IFrame, messageCallbackType, StompConfig, StompSubscription } from '@stomp/stompjs';
import { SiteConstants } from '../models/site-constants';

@Injectable({
  providedIn: 'root'
})
export class StompMessagingService {

  stompClient: Client;
  endpointCallbacks: Map<string, messageCallbackType>;
  endpointSubscriptions: Map<string, StompSubscription>;
  onConnectFunction: Function | undefined;
  onErrorFunction: Function | undefined;

  constructor() { 
    let stompConfig: StompConfig = {
      brokerURL: 'ws://' + SiteConstants.getStompEndpoint(),
      connectHeaders: {},
      debug: function(str) {console.log(str);},
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    }
    this.stompClient = new Client(stompConfig);
    this.endpointCallbacks = new Map();
    this.endpointSubscriptions = new Map();
    let current: StompMessagingService = this;

    this.stompClient.onConnect = function(frame) {
      current.setupSocketConnections();
      if(current.onConnectFunction !== undefined) {
        current.onConnectFunction();
      }
    }
    this.stompClient.onStompError = function(frame) {
      current.onServiceError(frame);
    }
  }

  private setupSocketConnections(): void {
    let current: StompMessagingService = this;
    this.endpointCallbacks.forEach(function(value, key) {
      let subscription = current.stompClient.subscribe(key, value);
      current.endpointSubscriptions.set(key, subscription);
    });
  }

  private onServiceError(frame: IFrame):void {
    console.log('Broker reported error: ' + frame.headers['message']);
    console.log('Additional details: ' + frame.body);
  }

  public setupEndpointSubscription(endpoint: string, callback: messageCallbackType): void {
    this.endpointCallbacks.set(endpoint, callback);
  }

  public removeEndpointSubscription(endpoint: string): void {
    let subscription = this.endpointSubscriptions.get(endpoint);
    if(subscription !== undefined) {
      this.stompClient.unsubscribe(subscription.id);
    }
    
    this.endpointCallbacks.delete(endpoint);
  }

  public setOnConnect(onConnectFunction: Function): void {
    this.onConnectFunction = onConnectFunction;
  }

  public setOnError(onErrorFunction: Function): void {
    this.onErrorFunction = onErrorFunction;
  }

  public connect(): void {
    this.stompClient.activate();
  }

  public disconnect(): void {
    this.stompClient.deactivate();
  }

  public sendMessage(destination: string, message: any): void {
    this.stompClient.publish({
      destination: destination,
      body: JSON.stringify(message)
    });
  }

}
