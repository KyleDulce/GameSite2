import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GsButtonComponent } from './gs-button.component';

describe('GsButtonComponent', () => {
  let component: GsButtonComponent;
  let fixture: ComponentFixture<GsButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GsButtonComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GsButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
