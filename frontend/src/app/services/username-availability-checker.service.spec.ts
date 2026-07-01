import { TestBed } from '@angular/core/testing';

import { UsernameAvailabilityCheckerService } from '../username-availability-checker.service';

describe('UsernameAvailabilityCheckerService', () => {
  let service: UsernameAvailabilityCheckerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UsernameAvailabilityCheckerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
