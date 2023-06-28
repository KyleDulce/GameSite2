import Bimap from "../lib/dataStructures/Bimap";

export enum GameType {
    NULL = "null",
    Test = "Test",
}

const gameTypeStringMapping = new Bimap<GameType, number>([
    [GameType.NULL, -1],
    [GameType.Test, -2],
]);


export const validGameTypes: string[] = [
    GameType.Test
]

export function serializeGameTypeToNumber(gameType: GameType): number {
    return gameTypeStringMapping.getByKey(gameType) as number;
}

export function parseNumberToGameType(gameTypeNum: number): GameType {
    return gameTypeStringMapping.getByValue(gameTypeNum) as GameType;
}

export function isValidGameTypeNum(gameTypeNum: number): boolean {
    return gameTypeStringMapping.hasValue(gameTypeNum);
}

export enum RestEndpoints {
    GET_ROOM_LISTS = "/getRoomLists",
    POST_JOIN_ROOM = "/joinRoom",
    POST_LEAVE_ROOM = "/leaveRoom",
    POST_CREATE_ROOM = "/createRoom",
    GET_INFO_ROOM = "/roomInfo",
    POST_UPDATE_USER = "/updateUser",
    GET_REFRESH_TOKEN = "/refreshToken",
    POST_AUTH = "/authenticate",
    DELETE_AUTH = "/invalidateAuthentication"
}

export enum SocketEndpoints {
    GAME_TOPIC = "/socket/topic/game",

    GAME_USER_QUEUE = "/socket/queue/gamePlayer",

    GAME_APP = "/socket/app/game"

}

export const DARK_MODE_HTML_ATTRIBUTE = 'darkMode';
export const MAX_CHAT_MESSAGES = 100;
