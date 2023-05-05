
export function doAllPropertiesExistInObject(obj: any, propKeys: string[]):boolean {
    if(obj == null) {
        return false;
    }
    for(let x = 0; x < propKeys.length; x++) {
        if(obj[propKeys[x]] == null) {
            return false;
        }
    }
    return true;
}
