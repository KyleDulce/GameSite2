import { Type } from "@angular/core";
import { BaseGameComponent } from "./base-game.component";
import { BaseGameSettingsComponent } from "./base-game-settings.component";

export abstract class BaseGameModule {
    public abstract getGameComponent(): Type<BaseGameComponent>;
    public abstract getSettingsComponent(): Type<BaseGameSettingsComponent>;
}