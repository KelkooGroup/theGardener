import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import {OpenApiModelComponent} from './open-api-model.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import {OpenApiModel} from '../../../_models/open-api';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';

describe('OpenApiModelComponent', () => {
  let component: OpenApiModelComponent;
  let fixture: ComponentFixture<OpenApiModelComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [OpenApiModelComponent],
      imports: [
        MatTableModule,
        MatSnackBarModule,
        NoopAnimationsModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OpenApiModelComponent);
    component = fixture.componentInstance;
    component.openApiModule = OPENAPI_MODULE;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});

const OPENAPI_MODULE: OpenApiModel = {
  modelName: 'modelName', required: ['name'], openApiRows: [{
    title: 'id',
    openApiType: 'integer',
    default: 'NONE',
    description: 'Id of the model',
    example: '10',
  }, {
    title: 'name',
    openApiType: 'string',
    default: 'NONE',
    description: 'Name of the model way too long ',
    example: 'project',
  }], childrenModels: [],
  errors: []
};
