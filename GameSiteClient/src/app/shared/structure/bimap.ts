
export default class Bimap<Key, Value> {
    private readonly keyMap: Map<Key, Value> = new Map();
    private readonly valueMap: Map<Value, Key> = new Map();

    constructor(from?: Array<readonly [Key, Value]>) {
        if(from == undefined) {
            return;
        }

        from.forEach(keyValuePair => {
            this.set(keyValuePair[0], keyValuePair[1]);
        }, this);
    }

    public clear(): void {
        this.keyMap.clear();
        this.valueMap.clear();
    }

    public deleteByKey(key: Key): boolean {
        if(!this.keyMap.has(key)) {
            return false;
        }

        const value = this.keyMap.get(key) as Value;
        this.keyMap.delete(key);
        this.valueMap.delete(value);
        return true;
    }

    public deleteByValue(value: Value): boolean {
        if(!this.valueMap.has(value)) {
            return false;
        }

        const key = this.valueMap.get(value) as Key;
        this.keyMap.delete(key);
        this.valueMap.delete(value);
        return true;
    }

    public entries(): Array<readonly [Key, Value]> {
        const entries: Array<readonly [Key, Value]> = []
        this.keyMap.forEach((value, key) => {
            entries.push([key, value]);
        })
        return entries;
    }

    public forEach(callbackFn: (value: Value, key: Key, map: Bimap<Key, Value>) => void): void {
        const self = this;
        this.keyMap.forEach((value: Value, key: Key) => {
            callbackFn(value, key, self);
        });
    }

    public getByKey(key: Key): Value | undefined {
        return this.keyMap.get(key);
    }

    public getByValue(value: Value): Key | undefined {
        return this.valueMap.get(value);
    }

    public hasKey(key: Key): boolean {
        return this.keyMap.has(key);
    }

    public hasValue(value: Value): boolean {
        return this.valueMap.has(value);
    }

    public keys(): Array<Key> {
        const keys: Array<Key> = [];
        this.keyMap.forEach((value, key) => {
            keys.push(key);
        })
        return keys;
    }

    public values(): Array<Value> {
        const values : Array<Value> = [];
        this.keyMap.forEach(value => {
            values.push(value);
        })
        return values;
    }

    public set(key: Key, value: Value) {
        // Check for duplicate either key or value
        // if either has a duplicate, delete
        if(this.keyMap.has(key)) {
            const oldValue = this.keyMap.get(key) as Value;
            this.keyMap.delete(key);
            this.valueMap.delete(oldValue);
        } 
        if(this.valueMap.has(value)) {
            const oldKey = this.valueMap.get(value) as Key;
            this.keyMap.delete(oldKey);
            this.valueMap.delete(value);
        }

        // Set to maps
        this.keyMap.set(key, value);
        this.valueMap.set(value, key);
    }

    public get size(): number {
        return this.keyMap.size;
    }

    public toString(): string {
        return "BiMap: " + this.entries().toString();
    }
}
