import React, { useState } from "react";
import { getConfigOptions } from "../services/StorageService";

export interface ConfigOptions {
    AuthToken: string | null;
    setAuthToken: Function;
    PlayerName: string | null;
    setPlayerName: Function;
    UseLightMode: boolean;
    setUseLightMode: Function;
}

export const EMPTY_CONFIG_OPTIONS: ConfigOptions = {
    AuthToken: process.env.REACT_APP_DEFAULT_AUTH_TOKEN === undefined ? null : process.env.REACT_APP_DEFAULT_AUTH_TOKEN ,
    setAuthToken: () => {},
    PlayerName: process.env.REACT_APP_DEFAULT_PLAYER_NAME === undefined ? null : process.env.REACT_APP_DEFAULT_PLAYER_NAME ,
    setPlayerName: () => {},
    UseLightMode: true,
    setUseLightMode: () => {}
}

export const ConfigContext = React.createContext<ConfigOptions>(EMPTY_CONFIG_OPTIONS);

export function ContextOptionProvider({children}: any) {
    const defaultConfig = getConfigOptions();
    const [authToken, setAuthToken] = useState(defaultConfig.AuthToken);
    const [playerName, setPlayerName] = useState(defaultConfig.PlayerName);
    const [useLightMode, setUseLightMode] = useState(defaultConfig.UseLightMode);

    const value: ConfigOptions = {
        AuthToken: authToken,
        setAuthToken: setAuthToken,
        PlayerName: playerName,
        setPlayerName: setPlayerName,
        UseLightMode: useLightMode,
        setUseLightMode: setUseLightMode
    }

    return (
        <>
            <ConfigContext.Provider value={value}>
                {children}
            </ConfigContext.Provider>
        </>
    )
}