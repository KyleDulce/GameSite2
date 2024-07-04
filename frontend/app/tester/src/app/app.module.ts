import { NgModule } from '@angular/core';
import { ElementsComponent } from './pages/elements/elements.component';

import { ElementsModule } from '@frontend/elements';
import { ComponentsModule } from '@frontend/components';
import { ControllerComponent } from './controller/controller.component';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { ComponentsComponent } from './pages/components/components.component';
@NgModule({
  imports: [ElementsModule, ComponentsModule, FormsModule, RouterModule, ReactiveFormsModule],
  declarations: [ElementsComponent, ControllerComponent, ComponentsComponent],
  exports: [
    ElementsComponent,
    ControllerComponent,
    CommonModule,
    ComponentsComponent,
  ],
})
export class AppModule {}
