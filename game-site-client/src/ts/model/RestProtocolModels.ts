import { GameType } from "./SystemConstants";
import { doAllPropertiesExistInObject } from './Utils';

export interface User {
    userid: number;
    name: string;
    isGuest: boolean;
}

export const emptyUser: User = {
    userid: 0,
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
    spectatorAmount: number;
    gameType: string; 
    hostName: string;
    inProgress: boolean;
    gameStartTime: number;
}

export const emptyRoomListingRaw: RoomListingRaw = {
    roomId: "",
    lobbySize: 0,
    maxLobbySize: 0,
    spectatorAmount: 0,
    gameType: "", 
    hostName: "",
    inProgress: false,
    gameStartTime: 0
}

export function isCompliantRoomListingRaw(obj: any): boolean {
    if(!doAllPropertiesExistInObject(obj, Object.keys(emptyRoomListingRaw).map(key => {
        return key
    }))) {
        return false;
    }
    return Object.values(GameType).some((key: string) => key === obj.gameType);
}

export function rawListingToFullListing(listing: RoomListingRaw): RoomListing {
    //return listing as RoomListing;
    return {
        roomId: listing.roomId,
        lobbySize: listing.lobbySize,
        maxLobbySize: listing.maxLobbySize,
        spectatorAmount: listing.spectatorAmount,
        gameType: (<any>GameType)[listing.gameType],
        hostName: listing.hostName,
        inProgress: listing.inProgress,
        gameStartTime: listing.gameStartTime
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
}

export interface RoomJoinRequest {
    user: User;
    asSpectator: boolean;
    roomId: string;
}

export const emptyRoomJoinRequest: RoomJoinRequest = {
    user: emptyUser,
    asSpectator: false,
    roomId: ""
}

export interface RoomLeaveRequest {
    user: User;
    roomId: string;
}

export const emptyRoomLeaveRequest: RoomLeaveRequest = {
    user: emptyUser,
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

export interface ChatMessage {
    messageText: string,
    senderName: string
}