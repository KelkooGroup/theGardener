import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GherkinStepComponent} from './gherkin-step.component';
import {GherkinTableComponent} from '../gherkin-table/gherkin-table.component';
import {GherkinLongTextComponent} from '../gherkin-long-text/gherkin-long-text.component';
import {MatExpansionModule, MatTableModule} from '@angular/material';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ExpandableNode, GherkinStep} from '../../../_models/gherkin';

describe('GherkinStepComponent', () => {
  let component: GherkinStepComponent;
  let fixture: ComponentFixture<GherkinStepComponent>;
  const step: GherkinStep = {
    type: 'string',
    nodeId: 'string',
    localId: 'string',
    data: {
      id: '0',
      keyword: 'Given',
      text: 'a user',
      argument: [],
    },
    hasTable: false,
    hasLongText: true,
    longText: 'string',
    text: null,
    table: {
      headers: {key: 'true', value: 'false'},
      headerIds: ['bjr'],
      rows: [{
        values: {key: 'clef'}, getValue(key: string): string {
          return 'c';
        }
      }],
      getHeader(headerId: string): string {
        return 'd';
      }

    },
    getChilden(): Array<ExpandableNode> {
      return new Array<ExpandableNode>();
    },
    hasChilden(): boolean {
      return false;
    }
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GherkinStepComponent,
        GherkinTableComponent,
        GherkinLongTextComponent,
      ], imports: [
        MatExpansionModule,
        MatTableModule,
        NgxJsonViewerModule,
        NoopAnimationsModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GherkinStepComponent);
    component = fixture.componentInstance;
    component.step = step;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
