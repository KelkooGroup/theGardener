import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {OpenApiModelComponent} from './open-api-model.component';

describe('OpenApiModelComponent', () => {
  let component: OpenApiModelComponent;
  let fixture: ComponentFixture<OpenApiModelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [OpenApiModelComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OpenApiModelComponent);
    component = fixture.componentInstance;
    component.dataSource = OPENAPI_MODULE;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});

const OPENAPI_MODULE = {model:[{  title: "id",
    type: "integer",
    default: "NONE",
    description: "Id of the model",
    example: "10",
  }, {
    title: "name",
    type: "string",
    default: "NONE",
    description: "Name of the model way too long ",
    example: "project",
  }]};

