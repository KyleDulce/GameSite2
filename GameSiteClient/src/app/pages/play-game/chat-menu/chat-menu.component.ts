import { Component } from "@angular/core";
import { tap } from "rxjs";
import { ChatMessageData, CommonGameTypeStrings } from "src/app/shared/models/socket.model";
import { ConfigurationService } from "src/app/shared/services/configuration.service";
import { GameSocketService } from "src/app/shared/services/game-socket.service";
import { environment } from "src/environments/environment";

@Component({
    selector: 'gs-chat-menu',
    templateUrl: './chat-menu.component.html',
    styleUrls: ['./chat-menu.component.scss'],
  })
export class ChatMenuComponent {
    public messages: Array<ChatMessageData> = [];
    public currentMessage: string = '';

    constructor(private gameSocketService: GameSocketService, private configuration: ConfigurationService) {
        gameSocketService.chatMessages
            .pipe(
                tap( message => {
                    this.messages.push(message);
                    if(this.messages.length > environment.maxChatMessages) {
                        this.messages.shift();
                    }
                })
            ).subscribe();
    }

    public onMessageSend(): void {
        const messageData: ChatMessageData = {
            message: this.currentMessage,
            senderName: this.configuration.playerName?? 'No Name'
        }
        this.gameSocketService.sendGameMessage(CommonGameTypeStrings.CHAT_MESSAGE_DATA, messageData);
        this.currentMessage = '';
    }
}
