import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { SiteConstants } from '../models/site-constants';

@Injectable({
  providedIn: 'root'
})
export class RestMessagingService {

  constructor(private http: HttpClient) {}

  public get(endpoint: string): Promise<any> {
    let current = this;
    return this.setupPromise(() => {
      return current.http.get("http://" + SiteConstants.getHostname() + endpoint);
    }, endpoint, "GET")
  }

  public post(endpoint: string, body: any): Promise<any> {
    let current = this;
    return this.setupPromise(() => {
      return current.http.post("http://" + SiteConstants.getHostname() + endpoint, body);
    }, endpoint, "POST")
  }

  public delete(endpoint: string, body: any): Promise<any> {
    let current = this;
    return this.setupPromise(() => {
      return current.http.delete("http://" + SiteConstants.getHostname() + endpoint, body);
    }, endpoint, "DELETE")
  }

  private setupPromise(request: () => Observable<Object>, endpoint: string, requestTypeString: string): Promise<any> {
    let resultPromise = new Promise(
      (resolve, reject) => {
        let observable = request();
        this.setupObservable(observable, resolve, reject, endpoint, requestTypeString);
      });
    return resultPromise;
  }

  private setupObservable(observable: Observable<any>,
                                resolve: (value: unknown) => void, 
                                reject: (value: unknown) => void,
                                endpoint: string,
                                requestType: string): void {
    let subscription = observable.subscribe({
          next(value: Object): void {
            subscription.unsubscribe();
            resolve(value);
          },
          error(err: any) {
            console.log("An error occured on " + requestType + " " + endpoint);
            console.log(err);
            subscription.unsubscribe();
            reject(err);
          }
    });
  }
}
