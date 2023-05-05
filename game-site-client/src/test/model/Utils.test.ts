import { doAllPropertiesExistInObject } from "../../ts/model/Utils";

describe('doAllPropertiesExistInObject', () => {

    it('should return true if all properties are not null or undefined', () => {
        let obj = {
            a: "value",
            b: "value",
            c: "value"
        };
        let propKeys: string[] = ["a", "b", "c"];

        let actual: boolean = doAllPropertiesExistInObject(obj, propKeys);

        expect(actual).toBeTruthy();
    });

    it('should return false if any property is null', () => {
        let obj = {
            a: "value",
            b: null,
            c: "value"
        };
        let propKeys: string[] = ["a", "b", "c"];

        let actual: boolean = doAllPropertiesExistInObject(obj, propKeys);

        expect(actual).toBeFalsy();
    });

    it('should return false if any property is undefined', () => {
        let obj = {
            a: "value",
            c: "value"
        };
        let propKeys: string[] = ["a", "b", "c"];

        let actual: boolean = doAllPropertiesExistInObject(obj, propKeys);

        expect(actual).toBeFalsy();
    });

    it('should return false if given object is null', () => {
        let propKeys: string[] = ["a", "b", "c"];

        let actual: boolean = doAllPropertiesExistInObject(null, propKeys);

        expect(actual).toBeFalsy();
    });

    it('should return false if given object is undefined', () => {
        let propKeys: string[] = ["a", "b", "c"];

        let actual: boolean = doAllPropertiesExistInObject(undefined, propKeys);

        expect(actual).toBeFalsy();
    });
});
