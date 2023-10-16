import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NullGameComponent } from './null-game.component';

describe('NullGameComponent', () => {
  let component: NullGameComponent;
  let fixture: ComponentFixture<NullGameComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NullGameComponent]
    });
    fixture = TestBed.createComponent(NullGameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
