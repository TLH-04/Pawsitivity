import { TestBed } from '@angular/core/testing';

import { Popupservice } from './popupservice';

describe('Popupservice', () => {
  let service: Popupservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Popupservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
