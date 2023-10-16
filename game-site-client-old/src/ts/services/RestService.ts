import axios, { AxiosError } from "axios";
import { axiosConfig,
    RoomCreateData, 
    RoomCreateResponse, 
    RoomJoinRequest, 
    RoomLeaveRequest, 
    RoomListing, 
    RoomListingRaw, 
    isCompliantRoomListingRaw, 
    isCompliantRoomCreateResponse, 
    rawListingToFullListing, 
    RestError,
    UserUpdateRequest,
    UserUpdateResponse,
    UserAuthRequest,
    UserAuthResponse,
    isCompliantUserAuthResponse,
    RestStatusCode,
    getStatusCodeFromNumber,
    RestMessage,
    RoomInfoResponse,
    isCompliantRoomInfoResponse,
    RoomCreateDataRaw,
    RoomJoinResponse} from "../model/RestProtocolModels";
import { RestEndpoints, serializeGameTypeToNumber } from "../model/SystemConstants";
import { ConfigOptions, updateAuthCookie } from "../model/ConfigOptions";

axios.interceptors.response.use(
    response => {
        const statusCode = getStatusCodeFromNumber(response.status);

        if(statusCode === RestStatusCode.OK) {
            return response;
        }

        const error = new Error("Non-Successful Response", {
            cause: "Http Status is not 2xx"
        });
        let resultError: RestError = {
            error: error,
            message: error.message,
            cause: error.cause,
            name: "RestError",
            stack: error.stack,
            statusCode: statusCode
        }
        return Promise.reject(resultError);
    },
    (error: AxiosError) => {
        const statusCode = getStatusCodeFromNumber(error.response?.status);

        let resultError: RestError = {
            error: error,
            message: error.message,
            cause: error.cause,
            name: "Rest Call Error",
            stack: error.stack,
            statusCode: statusCode
        }
        return Promise.reject(resultError);
    }
)

export default class RestService {

    static getRoomLists(configOptions: ConfigOptions): Promise<RestMessage<RoomListing[]>> {
        return new Promise<RestMessage<RoomListing[]>>((resolve, reject) => {
            axios.get(RestEndpoints.GET_ROOM_LISTS, axiosConfig)
            .then((response) => {
                updateAuthCookie(configOptions);
                if(!Array.isArray(response.data)) {
                    const err: RestError = {
                        statusCode: RestStatusCode.OTHER,
                        error: undefined,
                        message: "Unexpected Response: Response was not an array!",
                        name: "RestError"
                    }
                    reject(err);
                    return;
                }
                let parsedRoomListings : RoomListing[] = response.data.reduce(
                    (accumulator: RoomListing[], value: any) => {
                        if(!isCompliantRoomListingRaw(value)) {
                            return accumulator;
                        }
                        accumulator.push(rawListingToFullListing(value as RoomListingRaw));
                        return accumulator;
                    }, []);
                resolve({
                    data: parsedRoomListings,
                    statusCode: getStatusCodeFromNumber(response.status)
                });
            })
            .catch((reason) => {
                reject(reason);
            });
        });   
    }

    static postJoinRoom(request: RoomJoinRequest, configOptions: ConfigOptions): Promise<RestMessage<RoomJoinResponse>> {
        return new Promise<RestMessage<RoomJoinResponse>>((resolve, reject) => {
            axios.post(RestEndpoints.POST_JOIN_ROOM, request, axiosConfig)
            .then((response) => {
                updateAuthCookie(configOptions);
                if(response.data == null || response.data.success == null || response.data.isHost == null) {
                    const err: RestError = {
                        statusCode: RestStatusCode.OTHER,
                        error: undefined,
                        message: "Unexpected response: expected data RoomJoinResponse is null or undefined",
                        name: "RestError"
                    }
                    reject(err);
                    return;
                }
                resolve({
                    data: response.data as RoomJoinResponse,
                    statusCode: getStatusCodeFromNumber(response.status)
                });
            })
            .catch((reason) => {
                reject(reason);
            });
        });
    }

    static postLeaveRoom(request: RoomLeaveRequest, configOptions: ConfigOptions): void {
        axios.post(RestEndpoints.POST_LEAVE_ROOM, request, axiosConfig)
        .then(response => {
            updateAuthCookie(configOptions);
        })
        .catch((reason) => {
            console.log(`Post leave request failed due to: ${reason.toString()}`);
        });
    }

    static postCreateRoom(request: RoomCreateData, configOptions: ConfigOptions): Promise<RestMessage<RoomCreateResponse>> {
        const rawData: RoomCreateDataRaw = {
            gameType: serializeGameTypeToNumber(request.gameType),
            host: request.host,
            maxLobbySize: request.maxLobbySize
        }
        return new Promise<RestMessage<RoomCreateResponse>>((resolve, reject) => {
            axios.post(RestEndpoints.POST_CREATE_ROOM, rawData, axiosConfig)
            .then((response) => {
                updateAuthCookie(configOptions);
                if(!isCompliantRoomCreateResponse(response.data)) {
                    const err: RestError = {
                        statusCode: RestStatusCode.OTHER,
                        error: undefined,
                        message: "Unexpcted Response: Response was not a roomCreateResponse",
                        name: "RestError"
                    }
                    reject(err);
                    return;
                }
                resolve({
                    data: response.data as RoomCreateResponse,
                    statusCode: getStatusCodeFromNumber(response.status)
                });
            })
            .catch((reason) => {
                reject(reason);
            });
        });
    }

    static getRoomInfo(roomId: string, configOptions: ConfigOptions): Promise<RestMessage<RoomInfoResponse>> {
        return new Promise<RestMessage<RoomInfoResponse>>((resolve, reject) => {
            axios.get(`${RestEndpoints.GET_INFO_ROOM}/${roomId}`, axiosConfig)
            .then(response => {
                updateAuthCookie(configOptions);
                if(!isCompliantRoomInfoResponse(response.data)) {
                    const err: RestError = {
                        statusCode: RestStatusCode.OTHER,
                        error: undefined,
                        message: "Unexpcted Response: Response was not a RoomInfoResponse",
                        name: "RestError"
                    }
                    reject(err);
                    return;
                }
                resolve({
                    data: response.data as RoomInfoResponse,
                    statusCode: getStatusCodeFromNumber(response.status)
                })
            })
            .catch(reason => {
                reject(reason);
            })
        })
    }

    static postUpdateUser(request: UserUpdateRequest, configOptions: ConfigOptions): Promise<RestMessage<UserUpdateResponse>> {
        return new Promise<RestMessage<UserUpdateResponse>>((resolve, reject) => {
            axios.post(RestEndpoints.POST_UPDATE_USER, request, axiosConfig)
            .then((response) => {
                updateAuthCookie(configOptions);
                if(response.data == undefined || response.data.success == undefined) {
                    const err: RestError = {
                        statusCode: RestStatusCode.OTHER,
                        error: undefined,
                        message: "Unexpected Response: Response was not valid!",
                        name: "RestError"
                    }
                    reject(err);
                    return;
                }
                resolve({
                    data: response.data as UserUpdateResponse,
                    statusCode: getStatusCodeFromNumber(response.status)
                });
            })
            .catch((reason) => {
                reject(reason);
            })
        });
    }
    
    static getRefreshToken(configOptions: ConfigOptions): void {
        axios.get(RestEndpoints.GET_REFRESH_TOKEN, axiosConfig)
        .then(response => {
            updateAuthCookie(configOptions);
        })
        .catch((reason) => {
            console.log(`Get refresh request failed due to: ${reason.toString()}`);
        })
    }

    static postAuth(request: UserAuthRequest, configOptions: ConfigOptions): Promise<RestMessage<UserAuthResponse>> {
        return new Promise<RestMessage<UserAuthResponse>>((resolve, reject) => {
            axios.post(RestEndpoints.POST_AUTH, request, axiosConfig)
            .then(response => {
                updateAuthCookie(configOptions);
                if(!isCompliantUserAuthResponse(response.data)) {
                    const err: RestError = {
                        statusCode: RestStatusCode.OTHER,
                        error: undefined,
                        message: "Unexpected Response: Response was not valid!",
                        name: "RestError"
                    }
                    reject(err);
                    return;
                }
                configOptions.setPlayerName((response.data as UserAuthResponse).user.name);
                configOptions.setUid((response.data as UserAuthResponse).user.uuid);
                resolve({
                    data: response.data as UserAuthResponse,
                    statusCode: getStatusCodeFromNumber(response.status)
                });
            })
            .catch((reason) => {
                reject(reason);
            })
        });
    }

    static deleteAuthToken(configOptions: ConfigOptions): void {
        axios.delete(RestEndpoints.DELETE_AUTH, axiosConfig)
        .then(response => {
            updateAuthCookie(configOptions);
        })
        .catch((reason) => {
            console.log(`Delete auth request failed due to: ${reason.toString()}`);
        })
    }
}
