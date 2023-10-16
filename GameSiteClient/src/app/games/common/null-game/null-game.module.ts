import { NgModule, Type } from "@angular/core";
import { BaseGameModule } from "../base/base-game.module";
import { NullGameComponent } from "./null-game.component";
import { NullGameSettingsComponent } from "./null-game-settings.component";
import { BaseGameComponent } from "../base/base-game.component";
import { BaseGameSettingsComponent } from "../base/base-game-settings.component";

@NgModule({
    declarations: [
        NullGameComponent,
        NullGameSettingsComponent
    ],
    exports: [
        NullGameComponent,
        NullGameSettingsComponent
    ]
})
export class NullGameModule extends BaseGameModule {
    public getGameComponent(): Type<BaseGameComponent> {
        return NullGameComponent;
    }
    public getSettingsComponent(): Type<BaseGameSettingsComponent> {
        return NullGameSettingsComponent;
    }
}
