import { Route } from '@angular/router';
import { ControllerComponent } from './controller/controller.component';

export const appRoutes: Route[] = [
    {path: "**", component: ControllerComponent}
];
