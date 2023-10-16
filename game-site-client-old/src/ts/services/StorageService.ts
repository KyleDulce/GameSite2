import { ConfigOptions, EMPTY_CONFIG_OPTIONS } from "../model/ConfigOptions";

enum StorageConstants {
    CONFIG_AUTH = "gs-config-auth",
    CONFIG_NAME = "gs-config-name",
    CONFIG_UID = "gs-config-uid",
    CONFIG_USE_LIGHT = "gs-config-use-light"
}

export function saveConfigOptions(config: ConfigOptions) {
    if(config.AuthToken != null) {
        saveToStorage(StorageConstants.CONFIG_AUTH, config.AuthToken)
    }
    if(config.PlayerName != null) {
        saveToStorage(StorageConstants.CONFIG_NAME, config.PlayerName);
    }
    if(config.Uid != null) {
        saveToStorage(StorageConstants.CONFIG_UID, config.Uid);
    }
    saveToStorage(StorageConstants.CONFIG_USE_LIGHT, String(config.UseLightMode));
}

export function saveAuthCookie(auth: string | null) {
    if (auth != null) {
        saveToStorage(StorageConstants.CONFIG_AUTH, auth)
    } else {
        removeFromStorage(StorageConstants.CONFIG_AUTH);
    }
}

export function getConfigOptions(): ConfigOptions {
    const useLight = getFromStorage(StorageConstants.CONFIG_USE_LIGHT);
    if(useLight === null) {
        return EMPTY_CONFIG_OPTIONS;
    }

    const auth = getFromStorage(StorageConstants.CONFIG_AUTH);
    const name = getFromStorage(StorageConstants.CONFIG_NAME);
    const uid = getFromStorage(StorageConstants.CONFIG_UID);

    const result: ConfigOptions = EMPTY_CONFIG_OPTIONS;
    result.AuthToken = auth;
    result.PlayerName = name;
    result.Uid = uid;
    result.UseLightMode = useLight === "true";

    return result;
}

function saveToStorage(key: string, value: string) {
    if(process.env.REACT_APP_USE_SESSION_STORAGE == "true") {
        sessionStorage.setItem(key, value);
    } else {
        localStorage.setItem(key, value);
    }
}

function getFromStorage(key: string): string | null {
    if(process.env.REACT_APP_USE_SESSION_STORAGE == "true") {
        return sessionStorage.getItem(key);
    } else {
        return localStorage.getItem(key);
    }
}

function removeFromStorage(key: string) {
    if(process.env.REACT_APP_USE_SESSION_STORAGE == "true") {
        sessionStorage.removeItem(key);
    } else {
        localStorage.removeItem(key);
    }
}

function clearAllFromStorage() {
    if(process.env.REACT_APP_USE_SESSION_STORAGE == "true") {
        sessionStorage.clear();
    } else {
        localStorage.clear();
    }
}