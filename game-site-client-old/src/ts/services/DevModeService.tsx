import { SHA256 } from "crypto-js";
import { ConfigOptions } from "../model/ConfigOptions";
import RestService from "./RestService";
import { RestMessage, UserAuthResponse } from "../model/RestProtocolModels";
import { Navigate, Location, NavigateFunction } from "react-router-dom";

const HOME_PATH = "/";
const LOGIN_PATH = "/login";

const FALSE = "false";
const TRUE = "true";

export function appPageLayoutShouldRedirect(config: ConfigOptions, location: Location, containsAuth: boolean): {result: boolean, data: any} {
    if(process.env.NODE_ENV === 'production') {
        if(location.pathname !== LOGIN_PATH && !containsAuth) {
            return {result: true, data: <Navigate to={LOGIN_PATH} replace={false}/>}
        } else if(location.pathname === LOGIN_PATH && containsAuth) {
            return {result: true, data: <Navigate to={HOME_PATH} replace={true}/>}
        }
    } else {
        //not in the login screen, is only allowing authorized users and auth is not contained
        //if dev mode autolog autolog, otherwise go to login
        if(location.pathname !== LOGIN_PATH && process.env.REACT_APP_IGNORE_AUTH === FALSE && !containsAuth) {
            if(process.env.REACT_APP_AUTO_LOGIN && process.env.REACT_APP_AUTO_LOGIN !== FALSE) {
                devModeAutoLogin(config)
                ?.catch(error => {
                    console.log(error);
                })
            } else {
                console.log("falsy")
                return {result: true, data: <Navigate to={LOGIN_PATH} replace={false}/>}
            }
        //if we are in login screen and authentication is available, leave it, not required
        } else if(location.pathname === LOGIN_PATH && containsAuth && process.env.REACT_APP_IGNORE_AUTH === TRUE) {
            return {result: true, data: <Navigate to={HOME_PATH} replace={true}/>}
        }
    }

    return {result: false, data: null}
}

export function restCallOnUnauthorized(config: ConfigOptions, navigate: NavigateFunction) {
    if(process.env.NODE_ENV === 'production') {
        navigate(LOGIN_PATH);
    } else {
        if(process.env.REACT_APP_IGNORE_AUTH === FALSE) {
            devModeAutoLogin(config)
            ?.then(result => {
                navigate(0);
            }).catch(error => {
                console.error(error);
                navigate(LOGIN_PATH);
            })
        }
    }
}

function devModeAutoLogin(config: ConfigOptions): Promise<RestMessage<UserAuthResponse>> | undefined {
    if(process.env.NODE_ENV !== 'production') {
        console.log("Auto logging in via dev credentials in environment");
        const loginParams = process.env.REACT_APP_AUTO_LOGIN?.split(",");
        if (loginParams == undefined) {
            console.error("Credentials not available for dev mode!");
            return;
        }
        const hashedPassword = SHA256(loginParams[1]).toString();
        return RestService.postAuth({
            login: loginParams[0],
            passHash: hashedPassword
        }, config);
    }
    return undefined;
}
