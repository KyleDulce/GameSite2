import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { 
  Observable, 
  OperatorFunction, 
  Subject, 
  catchError, 
  map, 
  tap, 
  throwError 
} from 'rxjs';
import { 
  HttpOptions, 
  RestEndpoints, 
  RestError, 
  RestMessage, 
  RestStatusCode, 
  RoomCreateData, 
  RoomCreateDataRaw, 
  RoomCreateResponse, 
  RoomInfoResponse, 
  RoomJoinRequest, 
  RoomJoinResponse, 
  RoomLeaveRequest, 
  RoomListing, 
  RoomListingRaw, 
  UserAuthRequest, 
  UserAuthResponse, 
  UserUpdateRequest,
  UserUpdateResponse, 
  getStatusCodeFromNumber, 
  rawListingToFullListing 
} from '../models/rest-api.model';
import { serializeGameTypeToNumber } from '../models/system.model';
import { environment } from 'src/environments/environment';
import { ConfigurationService } from './configuration.service';

@Injectable({
  providedIn: 'root',
})
export class RestApiService {
  constructor(private httpClient: HttpClient, 
    private configurationService: ConfigurationService) {}

  public getRoomLists(): Observable<RestMessage<RoomListing[]>> {
    return this.get<RoomListingRaw[]>(RestEndpoints.GET_ROOM_LISTS)
      .pipe(
        this.mapToRestMessage<RoomListingRaw[]>(),
        map(response => {
          const parsedRoomListings: RoomListing[] = response.data.reduce(
            (accumulator: RoomListing[], value: RoomListingRaw) => {
              accumulator.push(rawListingToFullListing(value))
              return accumulator;
            }, []
          );

          const result: RestMessage<RoomListing[]> = {
            data: parsedRoomListings,
            statusCode: response.statusCode
          };

          return result;
        }),
        this.mapErrors<RestMessage<RoomListing[]>>()
      );
  }

  public postJoinRoom(request: RoomJoinRequest): Observable<RestMessage<RoomJoinResponse>> {
    return this.post<RoomJoinResponse>(RestEndpoints.POST_JOIN_ROOM, request)
      .pipe(
        this.mapToRestMessage<RoomJoinResponse>(),
        this.mapErrors<RestMessage<RoomJoinResponse>>()
      );
  }

  public postLeaveRoom(request: RoomLeaveRequest): void {
    this.post(RestEndpoints.POST_LEAVE_ROOM, request)
      .pipe(
        this.mapErrors<any>()
      ).subscribe({
        error: (err: RestError) => {
          console.error(err);
        }}
      );
  }

  public postCreateRoom(request: RoomCreateData): Observable<RestMessage<RoomCreateResponse>> {
    const rawData: RoomCreateDataRaw = {
      gameType: serializeGameTypeToNumber(request.gameType),
      host: request.host,
      maxLobbySize: request.maxLobbySize
    };

    return this.post<RoomCreateResponse>(RestEndpoints.POST_CREATE_ROOM, rawData)
      .pipe(
        this.mapToRestMessage<RoomCreateResponse>(),
        this.mapErrors<RestMessage<RoomCreateResponse>>()
      );
  }

  public getRoomInfo(roomId: string): Observable<RestMessage<RoomInfoResponse>> {
    return this.get<RoomInfoResponse>(`${RestEndpoints.GET_INFO_ROOM}/${roomId}`)
      .pipe(
        this.mapToRestMessage<RoomInfoResponse>(),
        this.mapErrors<RestMessage<RoomInfoResponse>>()
      )
  }

  public postUpdateUser(request: UserUpdateRequest): Observable<RestMessage<UserUpdateResponse>> {
    return this.post<UserUpdateResponse>(RestEndpoints.POST_UPDATE_USER, request)
      .pipe(
        this.mapToRestMessage<UserUpdateResponse>(),
        this.mapErrors<RestMessage<UserUpdateResponse>>()
      );
  }

  public getRefreshToken(): Observable<boolean> {
    const resultSubject: Subject<boolean> = new Subject();
    this.get<UserAuthResponse>(RestEndpoints.GET_REFRESH_TOKEN)
      .pipe(
        this.mapToRestMessage<UserAuthResponse>(true),
        this.mapErrors<RestMessage<UserAuthResponse>>(),
        tap(response => {
          this.configurationService.playerName = response.data.user.name
          this.configurationService.uid = response.data.user.uuid;
        })
      ).subscribe({
        next: (() => {
          resultSubject.next(true);
          resultSubject.complete();
        }),
        error: (err: RestError) => {
          console.error(err);
          resultSubject.next(false);
          resultSubject.complete();
        }}
      );

    return resultSubject.asObservable();
  }

  public postAuth(request: UserAuthRequest): Observable<RestMessage<UserAuthResponse>> {
    return this.post<UserAuthResponse>(RestEndpoints.POST_AUTH, request)
      .pipe(
        this.mapToRestMessage<UserAuthResponse>(),
        this.mapErrors<RestMessage<UserAuthResponse>>(),
        tap(response => {
          this.configurationService.playerName = response.data.user.name
          this.configurationService.uid = response.data.user.uuid;
        })
      );
  }

  public deleteAuthToken(): void {
    this.delete(RestEndpoints.DELETE_AUTH)
      .pipe(
        this.mapErrors<any>()
      ).subscribe({
        error: (err: RestError) => {
          console.error(err);
        }}
      );
  }

  private get<T>(endpoint: string): Observable<HttpResponse<T>> {
    return this.httpClient.get<T>(environment.backendUrl + environment.restEndpoint + endpoint, this.generateHttpOptions());
  }

  private post<T>(endpoint: string, body: any): Observable<HttpResponse<T>> {
    return this.httpClient.post<T>(environment.backendUrl + environment.restEndpoint + endpoint, body, this.generateHttpOptions());
  }

  private delete<T>(endpoint: string): Observable<HttpResponse<T>> {
    return this.httpClient.delete<T>(environment.backendUrl + environment.restEndpoint + endpoint, this.generateHttpOptions());
  }

  private mapToRestMessage<T>(ignoreBody: boolean = false): OperatorFunction<HttpResponse<T>, RestMessage<T>> {
    return map((data: HttpResponse<T>) => {

      const statusCode = getStatusCodeFromNumber(data.status);

      if(statusCode !== RestStatusCode.OK) {
        throw this.generateNonSuccessError(statusCode);
      }

      if(ignoreBody) {
        return {
          data: data.body as T,
          statusCode: statusCode
        };
      }

      if(data.body == null) {
        const error: RestError = {
          statusCode: RestStatusCode.OTHER,
          error: undefined,
          message: "Unexpected Response Format",
          name: "RestError"
        }

        throw error;
      }
      
      const restMessage: RestMessage<T> = {
        data: data.body,
        statusCode: statusCode
      }
      return restMessage;
    })
  }

  private mapErrors<T>(): OperatorFunction<any, T> {
    return catchError((error: Error | HttpErrorResponse) => {
      return throwError(() => {
        if (error.name === "RestError") {
          return error;
        }

        if(this.isHttpErrorResponse(error)) {
          return this.generateNonSuccessError(getStatusCodeFromNumber(error.status), error);
        }

        const restError: RestError = {
          statusCode: RestStatusCode.OTHER,
          error: error,
          message: "Unexpected Error",
          name: "RestError"
        };

        throw restError;
      });
    });
  }

  private isHttpErrorResponse(response: Error | HttpErrorResponse): response is HttpErrorResponse {
    return (response as HttpErrorResponse).status !== undefined;
  }

  private generateHttpOptions(): HttpOptions {
    return {
      withCredentials: true,
      observe: 'response',
      responseType: 'json'
    };
  }

  private generateNonSuccessError(statusCode: RestStatusCode, originalError: Error = new Error("Non-Successful Response", {
    cause: "Http Status is not 2xx"
  })): RestError {

    return {
      error: originalError,
      message: originalError.message,
      cause: originalError.cause,
      name: "RestError",
      stack: originalError.stack,
      statusCode: statusCode
    }
  }
}
