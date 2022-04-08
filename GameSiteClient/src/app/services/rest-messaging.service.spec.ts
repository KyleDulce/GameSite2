import { TestBed } from '@angular/core/testing';

import { RestMessagingService } from './rest-messaging.service';

describe('RestMessagingService', () => {
  let service: RestMessagingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RestMessagingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
