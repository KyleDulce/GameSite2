import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NullGameSettingsComponent } from './null-game-settings.component';

describe('NullGameSettingsComponent', () => {
  let component: NullGameSettingsComponent;
  let fixture: ComponentFixture<NullGameSettingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NullGameSettingsComponent]
    });
    fixture = TestBed.createComponent(NullGameSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
