import { Injectable } from '@angular/core';

@Injectable()
export class NativeHtmlService {
  public setAttributeToRoot(name: string, value: string | undefined = 'true') {
    const html = document.getElementsByTagName('html').item(0);
    html?.setAttribute(name, value);
  }

  public removeAttributeFromRoot(name: string) {
    const html = document.getElementsByTagName('html').item(0);
    if (html?.hasAttribute(name)) {
      html.removeAttribute(name);
    }
  }

  public getCookieValue(key: string): string | null {
    return this.cookieValueFromString(document.cookie, key);
  }

  public containsCookie(key: string): boolean {
    return this.getCookieValue(key) != null;
  }

  private cookieValueFromString(
    cookieString: string,
    key: string
  ): string | null {
    let match = cookieString.match(new RegExp('(^| )' + key + '=([^;]+)'));
    if (match) {
      return match[2];
    } else {
      return null;
    }
  }
}
