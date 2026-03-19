import { TestBed } from '@angular/core/testing';

import { AdminRoleNameService } from './admin-role-name.service';

describe('AdminRoleNameService', () => {
  let service: AdminRoleNameService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdminRoleNameService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
