import { Type } from "@angular/core";

export function doAllPropertiesExistInObject(obj: any, propKeys: string[]):boolean {
    if(obj == null) {
        return false;
    }
    for(let x = 0; x < propKeys.length; x++) {
        if(obj[propKeys[x]] === undefined) {
            return false;
        }
    }
    return true;
}

export interface ComponentTypes {
    game: Type<any>,
    settings: Type<any>
}