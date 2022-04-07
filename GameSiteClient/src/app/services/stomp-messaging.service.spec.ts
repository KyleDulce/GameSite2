import { TestBed } from '@angular/core/testing';

import { StompMessagingService } from './stomp-messaging.service';

describe('StompMessagingService', () => {
  let service: StompMessagingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StompMessagingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
