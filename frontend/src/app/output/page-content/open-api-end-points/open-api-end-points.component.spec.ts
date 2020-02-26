import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OpenApiEndPointsComponent } from './open-api-end-points.component';

describe('OpenApiEndPointsComponent', () => {
  let component: OpenApiEndPointsComponent;
  let fixture: ComponentFixture<OpenApiEndPointsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OpenApiEndPointsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OpenApiEndPointsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
