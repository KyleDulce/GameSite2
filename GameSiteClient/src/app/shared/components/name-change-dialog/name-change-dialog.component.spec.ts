import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NameChangeDialogComponent } from './name-change-dialog.component';

describe('NameChangeDialogComponent', () => {
  let component: NameChangeDialogComponent;
  let fixture: ComponentFixture<NameChangeDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NameChangeDialogComponent]
    });
    fixture = TestBed.createComponent(NameChangeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
