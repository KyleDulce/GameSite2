import { TestBed } from '@angular/core/testing';

import { DevModeService } from './dev-mode.service';

describe('DevModeService', () => {
  let service: DevModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DevModeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
