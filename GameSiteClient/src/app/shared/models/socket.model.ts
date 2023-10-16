import { User } from "./rest-api.model";
import { doAllPropertiesExistInObject } from "./utils.model";

export enum SocketEndpoints {
    GAME_TOPIC = "/socket/topic/game",
    GAME_USER_QUEUE = "/socket/queue/gamePlayer",
    GAME_APP = "/socket/app/game"
}

export const MAX_CHAT_MESSAGES = 100;

export interface ISocketMessage {
    body: any
}

// Game Socket Models
export class CommonGameTypeStrings {
    public static readonly CHAT_MESSAGE_DATA = "ChatMessageData";
    public static readonly JOIN_ROOM = "JoinRoomData";
    public static readonly SETTINGS_DATA_REQUEST = "SettingsDataRequest";
    public static readonly SETTINGS_DATA_RESPONSE = "SettingsDataResponse";
    public static readonly KICK_PLAYER_DATA = "KickPlayerData";
    public static readonly FORCE_KICK_DATA = "ForceKickData";
    public static readonly HOST_CHANGE_DATA = "HostChangeData";
}

export interface InvalidSocketMessage {
    code: number,
    message: string
}

const emptyInvalidSocketMessage: InvalidSocketMessage = {
    code: 0,
    message: ''
}

export function isCompliantInvalidSocketMessage(obj: any): boolean {
    return doAllPropertiesExistInObject(obj, Object.keys(emptyInvalidSocketMessage).map(key => {
        return key
    }));
}

export interface GameDataMessage {
    gameDataIdString: string,
    roomId: string,
    data: any
}

const emptyGameDataMessage: GameDataMessage = {
    gameDataIdString: "",
    roomId: "",
    data: null
}

export function isCompliantGameDataMessage(obj: any): boolean {
    return doAllPropertiesExistInObject(obj, Object.keys(emptyGameDataMessage).map(key => {
        return key
    }));
}

export interface GameDataUpdate {
    authToken: string,
    gameData: GameDataMessage
}

export interface ChatMessageData {
    message: string,
    senderName: string
}

export interface SettingsDataResponse {
    players: Array<User>;
}

export interface KickPlayerData {
    player: User
}
