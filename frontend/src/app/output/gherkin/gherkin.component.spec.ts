import {GherkinComponent} from './gherkin.component';
import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MatTableModule, MatTabsModule} from '@angular/material';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {GherkinStepComponent} from './gherkin-step/gherkin-step.component';
import {GherkinTableComponent} from './gherkin-table/gherkin-table.component';
import {GherkinLongTextComponent} from './gherkin-long-text/gherkin-long-text.component';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {GherkinScenario, Scenario} from '../../_models/gherkin';
import {SafePipe} from "../../safe.pipe";
import {InternalLinkPipe} from "../../internal-link.pipe";
import {AnchorPipe} from "../../anchor.pipe";
import {RemoveHtmlSanitizerPipe} from "../../removehtmlsanitizer.pipe";
import {MarkdownModule} from "ngx-markdown";


describe('GherkinComponent', () => {
  let component: GherkinComponent;
  let fixture: ComponentFixture<GherkinComponent>;
  let page: Page;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GherkinComponent,
        GherkinStepComponent,
        GherkinTableComponent,
        GherkinLongTextComponent,
        SafePipe,
        InternalLinkPipe,
        AnchorPipe,
        RemoveHtmlSanitizerPipe,
      ], imports: [
        MatTableModule,
        NoopAnimationsModule,
        NgxJsonViewerModule,
        MatTabsModule,
        MarkdownModule.forRoot(),
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GherkinComponent);
    component = fixture.componentInstance;
    component.scenarios = SCENARIOS;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(page.background).toBeTruthy();
    expect(page.backgroundSteps.length).toBe(1);

    expect(page.scenarios.length).toBe(3);
    expect(page.examples.length).toBe(0);
    expect(page.getScenarioIcon(0)).toEqual(['scenario-icon far fa-clipboard fa-sm']);
    expect(page.getScenarioIcon(1)).toEqual(['scenario-icon far fa-clipboard fa-sm', 'tag fas fa-times-circle fa-sm']);
    expect(page.getScenarioIcon(2)).toEqual(['scenario-icon far fa-clipboard fa-sm', 'tag fas fa-cogs fa-sm', 'tag fas fa-exclamation-triangle fa-sm']);
  });
});

class Page {
  constructor(private fixture: ComponentFixture<GherkinComponent>) {

  }

  get background(): HTMLElement {
    return this.fixture.nativeElement.querySelector('.background');
  }

  get backgroundSteps() {
    return this.background.querySelectorAll('app-gherkin-step');
  }

  get scenarios() {
    return this.fixture.nativeElement.querySelectorAll('.scenario');
  }

  getScenarioIcon(index: number) {
    const res: Array<string> = [];
    expect(this.scenarios.length).toBeGreaterThan(index);
    this.scenarios[index].querySelectorAll('i')
      .forEach((i: HTMLElement) => res.push(i.getAttribute('class')));
    return res;
  }

  get examples() {
    return this.fixture.nativeElement.querySelectorAll('.examples');
  }
}

const NOMINAL_LEVEL0_SCENARIO: GherkinScenario = {
  id: '1',
  name: '',
  keyword: '',
  description: '',
  workflowStep: '',
  abstractionLevel: 'level_0',
  caseType: 'nominal',
  steps: [],
  tags: []
};
const ERROR_LEVEL1_SCENARIO: GherkinScenario = {
  id: '2',
  name: '',
  keyword: '',
  description: '',
  workflowStep: '',
  abstractionLevel: 'level_1',
  caseType: 'error',
  steps: [],
  tags: []
};
const LIMIT_LEVEL_2_SCENARIO: GherkinScenario = {
  id: '3',
  name: '',
  keyword: '',
  description: '',
  workflowStep: '',
  abstractionLevel: 'level_2',
  caseType: 'limit',
  steps: [],
  tags: []
};

const SCENARIOS: Scenario = {
  id: '123',
  name: 'As a doc reader I want to be able to see scenarios',
  description: '',
  path: './',
  background: {
    id: '1',
    description: '',
    name: 'Background',
    keyword: 'Given',
    steps: [{
      id: '1',
      keyword: 'Given',
      text: 'The background step',
      argument: []
    }]
  },
  scenarios: [NOMINAL_LEVEL0_SCENARIO, ERROR_LEVEL1_SCENARIO, LIMIT_LEVEL_2_SCENARIO],
  branchId: 'qa',
  comments: [],
  tags: [],
  keyword: '',
  language: 'EN',
};
