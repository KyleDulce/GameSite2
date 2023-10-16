import { TestBed } from '@angular/core/testing';

import { GameLoadingService } from './game-loading.service';

describe('GameLoadingService', () => {
  let service: GameLoadingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GameLoadingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
