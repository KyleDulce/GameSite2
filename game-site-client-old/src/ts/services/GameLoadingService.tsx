import { GameType, parseNumberToGameType } from "../model/SystemConstants";
import { lazy } from 'react';
import RestService from "./RestService";
import { ConfigOptions } from "../model/ConfigOptions";
import { RestStatusCode } from "../model/RestProtocolModels";

const gameTypeCache: Map<string, GameType> = new Map();

export interface GameWindow {
    page: React.LazyExoticComponent<React.ComponentType<any>>
    settingPanel: React.LazyExoticComponent<React.ComponentType<any>>
}

function getGameTypeFiles(gameType: GameType) {
    switch(gameType) {
        case GameType.Test:
            return {
                page: import('../game/testGame/TestGamePage'),
                setting: import('../game/testGame/TestGameSettings')
            }
        default: 
            return {
                page: import('../game/nullGame/NullGamePage'),
                setting: import('../game/nullGame/NullGameSettings')
            }
    }
}

export function getGameWindowById(gameId: string, configOptions: ConfigOptions) : GameWindow {
    return {
        page: getGamePage(gameId, configOptions),
        settingPanel: getGameSettings(gameId, configOptions)
    }
}

function getGamePage(gameId: string, configOptions: ConfigOptions): React.LazyExoticComponent<React.ComponentType<any>> {
    return lazy(async () => {
        return (await getGameFilesFromGameId(gameId, configOptions)).page;
    });
}

function getGameSettings(gameId: string, configOptions: ConfigOptions): React.LazyExoticComponent<React.ComponentType<any>> {
    return lazy(async () => {
        return (await getGameFilesFromGameId(gameId, configOptions)).setting;
    });
}

async function getGameType(gameId: string, configOptions: ConfigOptions): Promise<GameType> {
    if(gameTypeCache.has(gameId)) {
        return gameTypeCache.get(gameId) as GameType;
    }

    const result = await RestService.getRoomInfo(gameId, configOptions);
    
    if(result.statusCode !== RestStatusCode.OK) {
        throw new Error("A valid room was not found with given params");
    }

    const gameTypeNum = result.data.room.gameType;
    return parseNumberToGameType(gameTypeNum);
}

async function getGameFilesFromGameId(gameId: string, configOptions: ConfigOptions) {
    const gameType = await getGameType(gameId, configOptions);
    return getGameTypeFiles(gameType);
}