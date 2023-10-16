import { Compiler, Injectable, NgModuleFactory, Type, createNgModule, createNgModuleRef } from '@angular/core';
import { Observable, from } from 'rxjs';
import { BaseGameModule } from 'src/app/games/common/base/base-game.module';
import { GameType } from 'src/app/shared/models/system.model';
import { ComponentTypes } from 'src/app/shared/models/utils.model';

interface ModuleRef {
  location: () => Promise<any>, 
  name: string
}

@Injectable()
export class GameLoadingService {

  private readonly gameTypeToImportMap: Map<GameType, ModuleRef> = new Map([
    [GameType.NULL, {location: () => import('../../games/common/null-game/null-game.module'), name: "NullGameModule"}],
    [GameType.TEST, {location: () => import('../../games/common/null-game/null-game.module'), name: "NullGameModule"}]
  ]);

  public loadGameModule(gameType: GameType): Observable<ComponentTypes> {
    return this.triggerImport(this.gameTypeToImportMap.get(gameType) as ModuleRef);
  }

  private triggerImport(gameProps: ModuleRef): Observable<ComponentTypes> {
    return from(
      (gameProps?.location())
        .then((importedModule) => {
          const gameModule = importedModule[gameProps?.name as string] as Type<any>;

          const moduleRef = createNgModule(gameModule).instance as unknown as BaseGameModule;
          return {
            game: moduleRef.getGameComponent(),
            settings: moduleRef.getSettingsComponent()
          }
        }));
  }
}
