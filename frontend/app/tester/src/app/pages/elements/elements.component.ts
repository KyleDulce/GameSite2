import { Component } from '@angular/core';

@Component({
  selector: 'tester-elements',
  templateUrl: './elements.component.html',
  styleUrl: './elements.component.scss',
})
export class ElementsComponent {
  public emitEvent(event: any): void {
    console.log(event);
  }
}
