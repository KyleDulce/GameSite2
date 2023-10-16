import { TestBed } from '@angular/core/testing';

import { NativeHtmlService } from './native-html.service';

describe('NativeHtmlService', () => {
  let service: NativeHtmlService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NativeHtmlService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
