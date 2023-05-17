import { GameType } from "../model/SystemConstants";
import { lazy } from 'react';

export interface GameWindow {
    page: React.LazyExoticComponent<React.ComponentType<any>>
    settingPanel: React.LazyExoticComponent<React.ComponentType<any>>
}

export function getGameWindow(gameType: GameType): GameWindow {
    if(gameType === GameType.JOIN_ROOM) {
        return {
            page: lazy(() => import('../game/testGame/TestGamePage')),
            settingPanel: lazy(() => import('../game/testGame/TestGameSettings'))
        };
    } else {
        return {
            page: lazy(() => import('../game/nullGame/NullGamePage')),
            settingPanel: lazy(() => import('../game/nullGame/NullGameSettings'))
        };
    }
}
