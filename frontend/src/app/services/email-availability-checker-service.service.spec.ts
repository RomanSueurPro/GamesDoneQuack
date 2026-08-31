import { TestBed } from '@angular/core/testing';

import { EmailAvailabilityCheckerServiceService } from './email-availability-checker-service.service';

describe('EmailAvailabilityCheckerServiceService', () => {
  let service: EmailAvailabilityCheckerServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmailAvailabilityCheckerServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
