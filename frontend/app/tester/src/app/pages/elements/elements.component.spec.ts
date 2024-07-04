import { TestBed } from '@angular/core/testing';
import { ElementsComponent } from './elements.component';

describe('ElementsComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ElementsComponent],
    }).compileComponents();
  });

  it('should create component', () => {
    const fixture = TestBed.createComponent(ElementsComponent);
    fixture.detectChanges();
  });
});
