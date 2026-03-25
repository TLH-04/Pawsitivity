import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MetricsDashboard } from './metrics-dashboard';

describe('MetricsDashboard', () => {
  let component: MetricsDashboard;
  let fixture: ComponentFixture<MetricsDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MetricsDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MetricsDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
