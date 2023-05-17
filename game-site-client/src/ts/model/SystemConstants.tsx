
export enum GameType {
    NULL = "null",
    JOIN_ROOM = "Test"
}

export const validGameTypes: GameType[] = [
    GameType.JOIN_ROOM
]

export enum RestEndpoints {
    GET_ROOM_LISTS = "/getRoomLists",
    POST_JOIN_ROOM = "/joinRoom",
    POST_LEAVE_ROOM = "/leaveRoom",
    POST_CREATE_ROOM = "/createRoom"
}

export const DARK_MODE_HTML_ATTRIBUTE = 'darkMode';
export const MAX_CHAT_MESSAGES = 100;
