import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ElementsModule } from '@frontend/elements';
import { GsMenuComponent } from './components/menu/menu.component';
import { CdkMenuModule } from '@angular/cdk/menu';
import { TextFieldComponent } from './components/text-field/text-field.component';
import { FormsModule } from '@angular/forms';
import { GsTextHintComponent } from './components/text-field/gs-text-hint/gs-text-hint.component';
import { GsTextErrorHintComponent } from './components/text-field/gs-text-error-hint/gs-text-error-hint.component';
import { GsTextAffixComponent } from './components/text-field/gs-text-affix/gs-text-affix.component';

@NgModule({
  imports: [CommonModule, ElementsModule, CdkMenuModule, FormsModule],
  declarations: [
    GsMenuComponent,
    TextFieldComponent,
    GsTextHintComponent,
    GsTextErrorHintComponent,
    GsTextAffixComponent,
  ],
  exports: [
    GsMenuComponent,
    TextFieldComponent,
    GsTextHintComponent,
    GsTextErrorHintComponent,
    GsTextAffixComponent,
  ],
})
export class ComponentsModule {}
