import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {GherkinStepComponent} from './gherkin-step.component';
import {GherkinTableComponent} from '../gherkin-table/gherkin-table.component';
import {GherkinLongTextComponent} from '../gherkin-long-text/gherkin-long-text.component';
import {MatExpansionModule, MatTableModule, MatTabsModule} from '@angular/material';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {GherkinStep} from '../../../_models/gherkin';
import {By} from '@angular/platform-browser';
import {SafePipe} from "../../../safe.pipe";
import {InternalLinkPipe} from "../../../internal-link.pipe";
import {AnchorPipe} from "../../../anchor.pipe";
import {RemoveHtmlSanitizerPipe} from "../../../removehtmlsanitizer.pipe";
import {MarkdownModule} from "ngx-markdown";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";


describe('GherkinStepComponent', () => {
  let component: GherkinStepComponent;
  let fixture: ComponentFixture<GherkinStepComponent>;
  let page: Page;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GherkinStepComponent,
        GherkinTableComponent,
        GherkinLongTextComponent,
        SafePipe,
        InternalLinkPipe,
        AnchorPipe,
        RemoveHtmlSanitizerPipe,
      ], imports: [
        MatExpansionModule,
        MatTableModule,
        MatTabsModule,
        NgxJsonViewerModule,
        NoopAnimationsModule,
        MarkdownModule.forRoot(),
        HttpClientTestingModule,
        RouterTestingModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GherkinStepComponent);
    page = new Page(fixture);
  });

  it('should show simple step', () => {
    const simpleStep: GherkinStep = {
      id: '4',
      keyword: 'Then',
      text: 'the suggestions are popular and available books adapted to the age of the user',
      argument: []
    };
    component = fixture.componentInstance;
    component.step = simpleStep;
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(component.longText).toBeFalsy();
    expect(component.textFragments).toEqual([{
      text: 'the suggestions are popular and available books adapted to the age of the user',
      isParameter: false
    }]);
    expect(component.table).toBeFalsy();
    expect(page.keyword).toEqual('Then');
    expect(page.stepText).toEqual(['the suggestions are popular and available books adapted to the age of the user']);
  });

  it('should show step with arguments', () => {
    const simpleStep: GherkinStep = {
      id: '0',
      keyword: 'Given',
      text: 'the user "Tim"',
      argument: []
    };
    component = fixture.componentInstance;
    component.step = simpleStep;
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(component.longText).toBeFalsy();
    expect(component.textFragments).toEqual([
      {text: 'the user ', isParameter: false},
      {text: 'Tim', isParameter: true},
    ]);
    expect(component.table).toBeFalsy();
    expect(page.keyword).toEqual('Given');
    expect(page.stepText).toEqual(['the user']);
    expect(page.stepParameters).toEqual(['Tim']);
  });

  it('should show step with multiple arguments', () => {
    const simpleStep: GherkinStep = {
      id: '0',
      keyword: 'Given',
      text: 'we ask for "3" suggestions from "2" different categories',
      argument: []
    };
    component = fixture.componentInstance;
    component.step = simpleStep;
    fixture.detectChanges();

    expect(page.keyword).toEqual('Given');
    expect(page.stepText).toEqual(['we ask for', 'suggestions from', 'different categories']);
    expect(page.stepParameters).toEqual(['3', '2']);
  });

  it('should show step with table', () => {
    const simpleStep: GherkinStep = {
      id: '2',
      keyword: 'And',
      text: 'the popular categories for this age are',
      argument: [
        ['categoryId', 'categoryName'],
        ['cat1', 'Walt Disney'],
        ['cat2', 'Picture books'],
        ['cat3', 'Bedtime stories']
      ]
    };
    component = fixture.componentInstance;
    component.step = simpleStep;
    fixture.detectChanges();

    expect(page.keyword).toEqual('And');
    expect(page.table).toBeTruthy();
    expect(page.stepText).toEqual(['the popular categories for this age are']);
    expect(page.table.table).toEqual([
      ['categoryId', 'categoryName'],
      ['cat1', 'Walt Disney'],
      ['cat2', 'Picture books'],
      ['cat3', 'Bedtime stories']
    ]);
  });

  it('should show step with long text', () => {
    const step: GherkinStep = {
      id: '0',
      keyword: 'Given',
      text: 'a user with description',
      argument: [
        [
          '   I\'m happy to read book from this library.\n   Having suggestions is a good idea.'
        ]
      ]
    };
    component = fixture.componentInstance;
    component.step = step;
    fixture.detectChanges();

    expect(page.keyword).toEqual('Given');
    expect(page.stepText).toEqual(['a user with description']);
    expect(page.longText.longText).toEqual('   I\'m happy to read book from this library.\n   Having suggestions is a good idea.');
  });
});

class Page {
  constructor(private fixture: ComponentFixture<GherkinStepComponent>) {

  }

  get keyword(): string {
    const keyword: HTMLElement = this.fixture.nativeElement.querySelector('.stepKeyword');
    expect(keyword).toBeTruthy();
    return keyword.textContent;
  }

  get textContent(): string {
    return this.fixture.nativeElement.textContent;
  }

  get stepParameters(): Array<string> {
    const res: Array<string> = [];
    this.fixture.nativeElement.querySelectorAll('.step-parameter')
      .forEach((e: HTMLElement) => {
        res.push(e.textContent.trim());
      });
    return res;
  }

  get stepText(): Array<string> {
    const res: Array<string> = [];
    this.fixture.nativeElement.querySelectorAll('.step-text')
      .forEach((e: HTMLElement) => {
        res.push(e.textContent.trim());
      });
    return res;
  }

  get table(): GherkinTableComponent {
    const table = this.fixture.debugElement.query(By.css('app-gherkin-table'));
    expect(table).toBeTruthy();
    return table.componentInstance as GherkinTableComponent;
  }

  get longText(): GherkinLongTextComponent {
    const longText = this.fixture.debugElement.query(By.css('app-gherkin-long-text'));
    expect(longText).toBeTruthy();
    return longText.componentInstance as GherkinLongTextComponent;
  }
}
