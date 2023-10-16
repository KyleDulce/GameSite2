import React, { useEffect, useState } from "react";
import { getConfigOptions, saveAuthCookie } from "../services/StorageService";
import { containsCookie, getCookieValue } from "../services/NativeHtmlService";

export interface ConfigOptions {
    AuthToken: string | null;
    setAuthToken: Function;
    PlayerName: string | null;
    setPlayerName: Function;
    Uid: string | null;
    setUid: Function;
    UseLightMode: boolean;
    setUseLightMode: Function;
}

export const EMPTY_CONFIG_OPTIONS: ConfigOptions = {
    AuthToken: process.env.REACT_APP_DEFAULT_AUTH_TOKEN === undefined ? null : process.env.REACT_APP_DEFAULT_AUTH_TOKEN ,
    setAuthToken: () => {},
    PlayerName: null,
    setPlayerName: () => {},
    Uid: null,
    setUid: () => {},
    UseLightMode: true,
    setUseLightMode: () => {}
}

const GAME_AUTH_COOKIE = "Game-AuthCookie";

export function updateAuthCookie(options: ConfigOptions) {
    let cookieVal = getCookieValue(GAME_AUTH_COOKIE);
    options.setAuthToken(cookieVal);
    saveAuthCookie(cookieVal);
}

export function containsGameAuth() {
    return containsCookie(GAME_AUTH_COOKIE);
}

export function getAuthCookie() {
    return getCookieValue(GAME_AUTH_COOKIE);
}

export const ConfigContext = React.createContext<ConfigOptions>(EMPTY_CONFIG_OPTIONS);

export function ContextOptionProvider({children}: any) {
    const defaultConfig = getConfigOptions();
    const [authToken, setAuthToken] = useState(defaultConfig.AuthToken);
    const [playerName, setPlayerName] = useState(defaultConfig.PlayerName);
    const [uid, setUId] = useState(defaultConfig.Uid);
    const [useLightMode, setUseLightMode] = useState(defaultConfig.UseLightMode);

    const value: ConfigOptions = {
        AuthToken: authToken,
        setAuthToken: setAuthToken,
        PlayerName: playerName,
        setPlayerName: setPlayerName,
        Uid: uid,
        setUid: setUId,
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