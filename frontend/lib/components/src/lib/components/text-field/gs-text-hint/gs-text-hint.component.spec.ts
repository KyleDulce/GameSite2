import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GsTextHintComponent } from './gs-text-hint.component';

describe('GsTextHintComponent', () => {
  let component: GsTextHintComponent;
  let fixture: ComponentFixture<GsTextHintComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GsTextHintComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GsTextHintComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
