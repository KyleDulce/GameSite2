import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActiveGameComponent } from './active-game.component';

describe('ActiveGameComponent', () => {
  let component: ActiveGameComponent;
  let fixture: ComponentFixture<ActiveGameComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActiveGameComponent]
    });
    fixture = TestBed.createComponent(ActiveGameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
