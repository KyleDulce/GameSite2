
export function setAttributeToRoot(name: string, value: string | undefined = 'true') {
    const html = document.getElementsByTagName('html').item(0);
    html?.setAttribute(name, value);
}

export function removeAttributeFromRoot(name: string) {
    const html = document.getElementsByTagName('html').item(0);
    if(html?.hasAttribute(name)) {
        html.removeAttribute(name);
    }
}

export function setCookie(cookieValue: string[]) {
    cookieValue.forEach(value => {
        document.cookie = value;
    });
}

export function getCookies(): string {
    return document.cookie;
}

export function getCookieValue(key: string): string | null {
    return cookieValueFromString(document.cookie, key);
}

export function cookieValueFromString(cookieString: string, key: string): string | null {
    let match = cookieString.match(new RegExp('(^| )' + key + '=([^;]+)'));
    if(match) {
        return match[2];
    } else {
        return null;
    }
}

export function containsCookie(key: string): boolean {
    return getCookieValue(key) != null;
}