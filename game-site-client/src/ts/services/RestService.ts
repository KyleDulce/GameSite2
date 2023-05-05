import axios from "axios";
import { RoomCreateData, 
    RoomCreateResponse, 
    RoomJoinRequest, 
    RoomLeaveRequest, 
    RoomListing, 
    RoomListingRaw, 
    isCompliantRoomListingRaw, 
    isCompliantRoomCreateResponse, 
    rawListingToFullListing } from "../model/RestProtocolModels";
import { RestEndpoints } from "../model/SystemConstants";

export default class RestService {

    static getRoomLists(): Promise<RoomListing[]> {
        return new Promise<RoomListing[]>((resolve, reject) => {
            axios.get(this.getRequestUrl(RestEndpoints.GET_ROOM_LISTS))
            .then((response) => {
                if(!Array.isArray(response.data)) {
                    reject("Unexpected Response: Response was not an array!");
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
                resolve(parsedRoomListings);
            })
            .catch((reason) => {
                reject(reason);
            });
        });   
    }

    static postJoinRoom(request: RoomJoinRequest): Promise<boolean> {
        return new Promise<boolean>((resolve, reject) => {
            axios.post(this.getRequestUrl(RestEndpoints.POST_JOIN_ROOM), request)
            .then((response) => {
                if(response.data == null || response.data.success == null) {
                    reject("Unexpected response: expected data 'success' is null or undefined");
                }
                resolve(response.data.success);
            })
            .catch((reason) => {
                reject(reason);
            });
        });
    }

    static postLeaveRoom(request: RoomLeaveRequest): void {
        axios.post(this.getRequestUrl(RestEndpoints.POST_LEAVE_ROOM), request)
        .catch((reason) => {
            console.log(`Post leave request failed due to: ${reason}`);
        });
    }

    static postCreateRoom(request: RoomCreateData): Promise<RoomCreateResponse> {
        return new Promise<RoomCreateResponse>((resolve, reject) => {
            axios.post(this.getRequestUrl(RestEndpoints.POST_CREATE_ROOM), request)
            .then((response) => {
                if(!isCompliantRoomCreateResponse(response.data)) {
                    reject("Unexpcted Response: Response was not a roomCreateResponse");
                }
                resolve(response.data as RoomCreateResponse);
            })
            .catch((reason) => {
                reject(reason);
            });
        });
    }

    private static getRequestUrl(endpoint: RestEndpoints): string {
        return (process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080') + (process.env.REACT_APP_REST_ENDPOINT || '') + endpoint;
    }
}
