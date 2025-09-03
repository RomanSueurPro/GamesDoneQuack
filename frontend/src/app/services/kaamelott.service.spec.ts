import { TestBed } from '@angular/core/testing';

import { KaamelottService } from './kaamelott.service';

describe('KaamelottService', () => {
  let service: KaamelottService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KaamelottService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
