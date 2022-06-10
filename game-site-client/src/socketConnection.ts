import SockJS from 'sockjs-client';
import {CompatClient, messageCallbackType, Stomp} from "@stomp/stompjs";

interface SubscriptionRequest{

    endpoint: string | undefined,
    callback: messageCallbackType

}

function connectAndSubscribeTo(subscriptions: Array<SubscriptionRequest>): Promise<CompatClient>{

    return new Promise<CompatClient>((resolve, reject) => {
        try{
            let client = Stomp.over(new SockJS((process.env.REACT_APP_BACKEND_URL || 'http://localhost:8080') + (process.env.REACT_APP_STOMP_ENDPOINT || '')));
            client.connect({}, (payload: any) => {

                for(let subscription of subscriptions) {
                    if (subscription.endpoint) {
                        client.subscribe(subscription.endpoint, subscription.callback);
                    }
                }
                resolve(client);

            });
        }catch(e){
            reject(e);
        }
    });

}

function sendTo(endpoint: string, client: CompatClient, body: any, headers: any){

    client.send(endpoint, headers, JSON.stringify(body));

}

export {
    connectAndSubscribeTo,
    sendTo
};
