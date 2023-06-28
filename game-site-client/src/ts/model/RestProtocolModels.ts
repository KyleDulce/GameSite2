import { AxiosRequestConfig } from "axios";
import { GameType, isValidGameTypeNum, parseNumberToGameType } from "./SystemConstants";
import { doAllPropertiesExistInObject } from './Utils';

export const axiosConfig: AxiosRequestConfig = {
    baseURL: (process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080') + (process.env.REACT_APP_REST_ENDPOINT || ''),
    withCredentials: true
}

export interface RestError extends Error {
    statusCode: RestStatusCode;
    message: string;
    error: Error | undefined;
}

export interface RestMessage<T> {
    data: T;
    statusCode: RestStatusCode;
}

export enum RestStatusCode {
    UNAUTHORIZED, NOT_FOUND, BAD_REQUEST, SERVER_ERROR, OTHER, 
    OK
}

export function getStatusCodeFromNumber(value: number | null | undefined): RestStatusCode {
    let status: RestStatusCode = RestStatusCode.OTHER;
    if(value != undefined) {
        if(value >= 200 && value < 300) {
            status = RestStatusCode.OK;
        } else if(value === 401 || value === 403) {
            status = RestStatusCode.UNAUTHORIZED;
        } else if(value === 404) {
            status = RestStatusCode.NOT_FOUND;
        } else if(value < 500 && value >= 400) {
            status = RestStatusCode.BAD_REQUEST;
        } else if(value < 600 && value >= 500) {
            status = RestStatusCode.SERVER_ERROR;
        } else {
            status = RestStatusCode.OTHER;
        }
    }
    return status;
}

export interface User {
    uuid: string;
    name: string;
    isGuest: boolean;
}

export const emptyUser: User = {
    uuid: "",
    name: "",
    isGuest: false
};

export function isCompliantUser(obj: any): boolean {
    return doAllPropertiesExistInObject(obj, Object.keys(emptyUser).map(
        keys => {
            return keys;
        }
    ));
}

export interface RoomListingRaw {
    roomId: string;
    lobbySize: number;
    maxLobbySize: number;
    spectatorsAmount: number;
    gameType: number; 
    hostName: string;
    inProgress: boolean;
    gameStartTime: number;
    roomName: string;
}

export const emptyRoomListingRaw: RoomListingRaw = {
    roomId: "",
    lobbySize: 0,
    maxLobbySize: 0,
    spectatorsAmount: 0,
    gameType: -1, 
    hostName: "",
    inProgress: false,
    gameStartTime: 0,
    roomName: ""
}

export function isCompliantRoomListingRaw(obj: any): boolean {
    if(!doAllPropertiesExistInObject(obj, Object.keys(emptyRoomListingRaw).map(key => {
        return key
    }))) {
        return false;
    }
    return isValidGameTypeNum(obj.gameType as number);
}

export function rawListingToFullListing(listing: RoomListingRaw): RoomListing {
    //return listing as RoomListing;
    return {
        roomId: listing.roomId,
        lobbySize: listing.lobbySize,
        maxLobbySize: listing.maxLobbySize,
        spectatorAmount: listing.spectatorsAmount,
        gameType: parseNumberToGameType(listing.gameType),
        hostName: listing.hostName,
        inProgress: listing.inProgress,
        gameStartTime: listing.gameStartTime,
        roomName: listing.roomName
    }
}

export interface RoomListing {
    roomId: string;
    lobbySize: number;
    maxLobbySize: number;
    spectatorAmount: number;
    gameType: GameType; 
    hostName: string;
    inProgress: boolean;
    gameStartTime: number;
    roomName: string;
}

export interface RoomJoinRequest {
    asSpectator: boolean;
    roomId: string;
}

export const emptyRoomJoinRequest: RoomJoinRequest = {
    asSpectator: false,
    roomId: ""
}

export interface RoomJoinResponse {
    success: boolean;
    isHost: boolean;
}

export interface RoomLeaveRequest {
    roomId: string;
}

export const emptyRoomLeaveRequest: RoomLeaveRequest = {
    roomId: ""
}

export interface RoomCreateDataRaw {
    maxLobbySize: number;
    gameType: number
    host: User;
}

export interface RoomCreateData {
    maxLobbySize: number;
    gameType: GameType
    host: User;
}

export const emptyRoomCreateData = {
    maxLobbySize: 0,
    gameType: GameType.NULL,
    host: emptyUser
}

export interface RoomCreateResponse {
    success: boolean;
    roomId: string; 
}

const emptyRoomCreateResponse = {
    success: false,
    roomId: "" 
}

export function isCompliantRoomCreateResponse(obj: any): boolean {
    return doAllPropertiesExistInObject(obj, Object.keys(emptyRoomCreateResponse).map(key => {
        return key
    }));
}

export interface UserUpdateRequest {
    name: string
}

export const emptyUserUpdateRequest = {
    name: ""
}

export interface UserUpdateResponse {
    success: boolean
}

export const emptyUserUpdateResponse = {
    success: false
}

export interface UserAuthRequest {
    login: string,
    passHash: string
}

export const emptyUserAuthRequest = {
    login: "",
    passHash: ""
}

export interface UserAuthResponse {
    success: boolean,
    user: User
}

export function isCompliantUserAuthResponse(obj: any): boolean {
    return doAllPropertiesExistInObject(obj, Object.keys(emptyUserAuthResponse).map(key => {
        return key
    }));
}

export const emptyUserAuthResponse = {
    success: false,
    user: emptyUser
}

export interface RoomInfoResponse {
    room: RoomListingRaw
    isHost: boolean
}

export const emptyRoomInfoResponse: RoomInfoResponse = {
    room: emptyRoomListingRaw,
    isHost: false
}

export function isCompliantRoomInfoResponse(obj: any): boolean {
    return doAllPropertiesExistInObject(obj, Object.keys(emptyRoomInfoResponse).map(key => {
        return key
    }));
}

export interface ChatMessage {
    messageText: string,
    senderName: string
}
