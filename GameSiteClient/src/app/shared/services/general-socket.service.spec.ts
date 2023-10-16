import { TestBed } from '@angular/core/testing';

import { GeneralSocketService } from './general-socket.service';

describe('GeneralSocketService', () => {
  let service: GeneralSocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GeneralSocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
