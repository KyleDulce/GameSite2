import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GsMenuComponent } from './menu.component';

describe('GsMenuComponent', () => {
  let component: GsMenuComponent;
  let fixture: ComponentFixture<GsMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GsMenuComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GsMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
