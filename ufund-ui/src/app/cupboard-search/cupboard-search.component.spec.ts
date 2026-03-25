import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CupboardSearchComponent } from './cupboard-search.component';

describe('CupboardSearchComponent', () => {
  let component: CupboardSearchComponent;
  let fixture: ComponentFixture<CupboardSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CupboardSearchComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CupboardSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
