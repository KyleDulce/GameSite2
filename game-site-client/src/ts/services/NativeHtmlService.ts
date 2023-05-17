
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
