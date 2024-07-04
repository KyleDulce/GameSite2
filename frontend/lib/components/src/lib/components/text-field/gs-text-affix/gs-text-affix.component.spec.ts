import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GsTextAffixComponent } from './gs-text-affix.component';

describe('GsTextAffixComponent', () => {
  let component: GsTextAffixComponent;
  let fixture: ComponentFixture<GsTextAffixComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GsTextAffixComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GsTextAffixComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
