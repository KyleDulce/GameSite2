import exp from "constants";
import axios from "../../../node_modules/axios";
import { RoomCreateResponse, RoomListing, RoomListingRaw, rawListingToFullListing } from "../../ts/model/RestProtocolModels";
import RestService from '../../ts/services/RestService';

jest.mock('axios');
const axiosMock = jest.mocked(axios);

afterEach(() => {
    jest.clearAllMocks();
});

describe('getRoomLists() -> Promise<RoomListing[]>', () => {
    it('should return a successful roomlisting object', async () => {
        let response: any = {data: [{
            gameStartTime: 1,
            gameType: "Test",
            hostName: "name",
            inProgress: false,
            lobbySize: 1,
            maxLobbySize: 1,
            roomId: "id",
            spectatorAmount: 1
        }]};
        let responsePromise: Promise<any> = Promise.resolve(response);
        let expected: RoomListing[] = [ rawListingToFullListing(response.data[0] as RoomListingRaw) ];
        axiosMock.get.mockImplementationOnce(() => {
            return responsePromise;
        });

        let actual = RestService.getRoomLists();

        expect(actual).resolves.toEqual(expected);
    });

    it('should reject on axios reject', async () => {
        const EXPECTED_ERROR = "This is a bad error";
        let response: Promise<any> = Promise.reject(new Error(EXPECTED_ERROR));
        axiosMock.get.mockImplementationOnce(() => {
            return response;
        });

        let actual = RestService.getRoomLists();
        
        expect(actual).rejects.toEqual(new Error(EXPECTED_ERROR));
    });

    it('should reject if resposne is not an array', async () => {
        const data = "I am not an array";
        let response: Promise<any> = Promise.resolve(data);
        axiosMock.get.mockImplementationOnce(() => {
            return response;
        });

        let actual = RestService.getRoomLists();

        expect(actual).rejects.toBeDefined();
    })

    it('should ignore non-compliant room listings', async () => {
        let response: any = {data: [{
            gameType: "Test",
            hostName: "name",
            inProgress: false,
            lobbySize: 1,
            maxLobbySize: 1,
            roomId: "id",
            spectatorAmount: 1
        }]};
        let responsePromise: Promise<any> = Promise.resolve(response);
        let expected: RoomListing[] = [ ];
        axiosMock.get.mockImplementationOnce(() => {
            return responsePromise;
        });

        let actual = RestService.getRoomLists();

        expect(actual).resolves.toEqual(expected);
    })
});

describe('postJoinRoom(RoomJoinRequest) -> Promise<boolean>', () => {
    it('should return true on success', async () => {
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.resolve({data: {success: true}});
        });

        let actual = RestService.postJoinRoom(null);

        expect(actual).resolves.toBe(true);
    });

    it('should return false on fail', async () => {
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.resolve({data: {success: false}});
        });

        let actual = RestService.postJoinRoom(null);

        expect(actual).resolves.toBe(false);
    });

    it('should reject when data is null', async () => {
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.resolve({data: null});
        });

        let actual = RestService.postJoinRoom(null);

        expect(actual).rejects.toBeDefined()
    });

    it('should reject when success is null', async () => {
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.resolve({data: {success: null}});
        });

        let actual = RestService.postJoinRoom(null);

        expect(actual).rejects.toBeDefined()
    });

    it('should reject axios rejects', async () => {
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.resolve({data: {success: null}});
        });

        let actual = RestService.postJoinRoom(null);

        expect(actual).rejects.toBeDefined()
    });
});

describe('postLeaveRoom(RoomLeaveRequest) -> void', () => {
    it('should produce no log on success', async () => {
        let logSpy = jest.spyOn(console, "log");
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.resolve("Success");
        });

        RestService.postLeaveRoom(null);
        await new Promise((r) => setTimeout(r, 1000));

        expect(logSpy).toHaveBeenCalledTimes(0);
    });

    it('should produce an error log on failure', async () => {
        let logSpy = jest.spyOn(console, "log");
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.reject(new Error("Failure"));
        });

        RestService.postLeaveRoom(null);
        await new Promise((r) => setTimeout(r, 1000));

        expect(logSpy).toHaveBeenCalledTimes(1);
    });
});

describe('postCreateRoom(RoomCreateData) -> Promise<RoomCreateResponse>', () => {
    it('should resolve created room on success', async () => {
        let expectedResponse: RoomCreateResponse = {
            success: true,
            roomId: "abc123"
        };
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.resolve({data: expectedResponse});
        });

        let actual = RestService.postCreateRoom(null);

        expect(actual).resolves.toEqual(expectedResponse);
    });

    it('should reject when not a valid response', async () => {
        let response: any = {
            success: true
        };
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.resolve({data: response});
        });

        let actual = RestService.postCreateRoom(null);

        expect(actual).rejects.toBeDefined();
    });

    it('should reject when request rejects', async () => {
        let error = "This is bad";
        axiosMock.post.mockImplementationOnce(() => {
            return Promise.reject(new Error(error));
        });

        let actual = RestService.postCreateRoom(null);

        expect(actual).rejects.toBeDefined();
    });
});
