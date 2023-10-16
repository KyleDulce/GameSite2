import { HttpHeaders } from '@angular/common/http';
import { 
  GameType, 
  parseNumberToGameType 
} from './system.model';

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

export interface HttpOptions {
  headers?:
    | HttpHeaders
    | {
        [header: string]: string | string[];
      }
    | undefined;
  withCredentials?: boolean | undefined;
  observe: 'response';
  responseType: 'json';
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

export interface RoomJoinRequest {
  asSpectator: boolean;
  roomId: string;
}

export interface RoomJoinResponse {
  success: boolean;
  isHost: boolean;
}

export interface RoomLeaveRequest {
  roomId: string;
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

export interface RoomCreateResponse {
  success: boolean;
  roomId: string; 
}

export interface UserUpdateRequest {
  name: string
}

export interface UserUpdateResponse {
  success: boolean
}

export interface UserAuthRequest {
  login: string,
  passHash: string
}

export interface UserAuthResponse {
  success: boolean,
  user: User
}

export interface RoomInfoResponse {
  room: RoomListingRaw
  isHost: boolean
  joinedRoom: boolean
  isSpectating: boolean
}

