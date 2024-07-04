import { Component } from '@angular/core';
import { MenuItem } from '@frontend/components';

@Component({
  selector: 'tester-components',
  templateUrl: './components.component.html',
  styleUrl: './components.component.scss',
})
export class ComponentsComponent {
  protected menuItems: MenuItem[] = [
    {
      menuItemId: "T1",
      text: "Testdaaddad 1",
      onClick: () => console.log("Execute T1")
    },
    {
      menuItemId: "T2",
      text: "Test 2",
      onClick: () => console.log("Execute T2")
    },
    {
      menuItemId: "T3",
      text: "Test 3",
      onClick: () => console.log("Execute T3")
    },
  ]

  public debugOut(details: string): void {
    console.log(`Debug: ${details}`);
  }
}
