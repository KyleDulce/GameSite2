import { NgModule } from '@angular/core';
import { GsButtonComponent } from './button/gs-button/gs-button.component';
import { CommonModule } from '@angular/common';
import { MatRippleModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { IconButtonComponent } from './icon-button/icon-button/icon-button.component';

@NgModule({
  imports: [CommonModule, MatRippleModule, MatIconModule, MatButtonModule],
  exports: [GsButtonComponent, IconButtonComponent],
  declarations: [GsButtonComponent, IconButtonComponent],
})
export class ElementsModule {}
