import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GherkinBackgroundComponent} from './gherkin-background.component';
import {MatExpansionModule, MatExpansionPanel, MatTableModule} from '@angular/material';
import {By} from '@angular/platform-browser';
import {GherkinStepComponent} from '../gherkin-step/gherkin-step.component';
import {GherkinTableComponent} from '../gherkin-table/gherkin-table.component';
import {GherkinLongTextComponent} from '../gherkin-long-text/gherkin-long-text.component';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {BrowserAnimationsModule, NoopAnimationsModule} from '@angular/platform-browser/animations';
import {GherkinBackground} from '../../../_models/hierarchy';

describe('GherkinBackgroundComponent', () => {
  let component: GherkinBackgroundComponent;
  let fixture: ComponentFixture<GherkinBackgroundComponent>;
  let page: Page;
  const background: GherkinBackground = {
    id: 'id',
    description: '',
    keyword: '',
    name: 'background',
    steps: [{
      id: '0',
      keyword: 'Given',
      text: 'a user',
      argument: []
    },{
      id: '1',
      keyword: 'When',
      text: 'we ask for suggestions',
      argument: []
    }]
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GherkinBackgroundComponent,
        GherkinStepComponent,
        GherkinTableComponent,
        GherkinLongTextComponent,
      ],
      imports: [
        MatExpansionModule,
        MatTableModule,
        NgxJsonViewerModule,
        NoopAnimationsModule,
        BrowserAnimationsModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GherkinBackgroundComponent);
    component = fixture.componentInstance;
    component.background = background;
    fixture.detectChanges();
    page = new Page(fixture);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(page.backgroundPanel.expanded).toBeTruthy();
    expect(page.gherkinSteps.length).toBe(2);
  });
});

class Page {
  constructor(private fixture: ComponentFixture<GherkinBackgroundComponent>) {

  }

  get backgroundPanel(): MatExpansionPanel {
    return this.fixture.debugElement.query(By.css('mat-expansion-panel')).componentInstance;
  }

  get gherkinSteps(): Array<HTMLElement> {
    return this.fixture.nativeElement.querySelectorAll('app-gherkin-step');
  }
}
