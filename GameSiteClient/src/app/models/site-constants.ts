import { environment } from "src/environments/environment";

export class SiteConstants {
    private static STOMP_ENDPOINT: string = "/socket/stomp";
    private static NON_PROD_HOST: string = "localhost:8080";

    public static getStompEndpoint(): string {
        return this.getHostname() + this.STOMP_ENDPOINT;
    }

    public static getHostname(): string {
        if (environment.production) {
            return window.location.hostname;
        }
        return this.NON_PROD_HOST;
    }
}