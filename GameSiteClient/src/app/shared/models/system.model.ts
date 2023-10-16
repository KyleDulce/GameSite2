import Bimap from "../structure/bimap";

export enum GameType {
    NULL = "null",
    TEST = "Test",
}

const gameTypeStringMapping = new Bimap<GameType, number>([
    [GameType.NULL, -1],
    [GameType.TEST, -2],
]);


export const validGameTypes: GameType[] = [
    GameType.TEST
];

export function serializeGameTypeToNumber(gameType: GameType): number {
    return gameTypeStringMapping.getByKey(gameType) as number;
}

export function parseNumberToGameType(gameTypeNum: number): GameType {
    return gameTypeStringMapping.getByValue(gameTypeNum) ?? GameType.NULL;
}

export function isValidGameTypeNum(gameTypeNum: number): boolean {
    return gameTypeStringMapping.hasValue(gameTypeNum);
}

export const DARK_MODE_HTML_ATTRIBUTE = 'darkMode';
