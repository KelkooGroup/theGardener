import { TestBed } from '@angular/core/testing';

import { MobileMenuHelperService } from './mobile-menu-helper.service';

describe('MobileMenuHelperService', () => {
  let service: MobileMenuHelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MobileMenuHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
