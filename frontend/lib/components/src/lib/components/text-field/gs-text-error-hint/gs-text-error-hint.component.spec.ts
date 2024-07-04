import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GsTextErrorHintComponent } from './gs-text-error-hint.component';

describe('GsTextErrorHintComponent', () => {
  let component: GsTextErrorHintComponent;
  let fixture: ComponentFixture<GsTextErrorHintComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GsTextErrorHintComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GsTextErrorHintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
