import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { GherkinTableComponent } from './gherkin-table.component';
import { MatTableModule } from '@angular/material/table';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MarkdownModule } from 'ngx-markdown';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { RouterTestingModule } from '@angular/router/testing';
import { SafePipe } from '../../../safe.pipe';
import { InternalLinkPipe } from '../../../internal-link.pipe';
import { AnchorPipe } from '../../../anchor.pipe';
import { RemoveHtmlSanitizerPipe } from '../../../removehtmlsanitizer.pipe';
import { SecurityContext } from '@angular/core';

describe('GherkinTableComponent', () => {
  let component: GherkinTableComponent;
  let fixture: ComponentFixture<GherkinTableComponent>;
  let page: Page;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GherkinTableComponent, SafePipe, InternalLinkPipe, AnchorPipe, RemoveHtmlSanitizerPipe],
      imports: [
        MatTableModule,
        HttpClientTestingModule,
        NoopAnimationsModule,
        MarkdownModule.forRoot({
          sanitize: SecurityContext.NONE
        }),
        NgxJsonViewerModule,
        RouterTestingModule
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GherkinTableComponent);
    component = fixture.componentInstance;
  });

  it('should show scenario', () => {
    component.table = TABLE;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(page.table).toBeTruthy();
    expect(page.tableHeader).toEqual(['categoryId', 'categoryName']);
    expect(page.tableRows.length).toBe(3);
    expect(page.getTableRow(0)).toEqual(['cat1', 'Walt Disney']);
    expect(page.getTableRow(1)).toEqual(['cat2', 'Picture books']);
    expect(page.getTableRow(2)).toEqual(['cat3', 'Bedtime stories']);
  });

  it('should show examples', () => {
    component.examples = EXAMPLES;
    page = new Page(fixture);
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(page.table).toBeTruthy();
    expect(page.tableHeader).toEqual(['categoryId', 'categoryName']);
    expect(page.tableRows.length).toBe(3);
    expect(page.getTableRow(0)).toEqual(['cat1', 'Walt Disney']);
    expect(page.getTableRow(1)).toEqual(['cat2', 'Picture books']);
    expect(page.getTableRow(2)).toEqual(['cat3', 'Bedtime stories']);
  });
});

class Page {
  constructor(private fixture: ComponentFixture<GherkinTableComponent>) {}

  get table(): HTMLElement {
    return this.fixture.nativeElement.querySelector('.gherkinTable');
  }

  get tableHeader(): Array<string> {
    const res: Array<string> = [];
    this.fixture.nativeElement.querySelectorAll('.gherkinCellHeaderTable').forEach((h: HTMLElement) => res.push(h.textContent.trim()));
    return res;
  }

  get tableRows(): Array<HTMLElement> {
    return this.fixture.nativeElement.querySelectorAll('.gherkinRowTable');
  }

  getTableRow(index: number): Array<string> {
    const rows: Array<HTMLElement> = this.tableRows;
    expect(rows.length > index);
    const res: Array<string> = [];
    rows[index].querySelectorAll('.gherkinCellTable').forEach(element => res.push(element.textContent.trim()));
    return res;
  }
}

const TABLE: Array<Array<string>> = [
  ['categoryId', 'categoryName'],
  ['cat1', 'Walt Disney'],
  ['cat2', 'Picture books'],
  ['cat3', 'Bedtime stories']
];

const EXAMPLES = [
  {
    id: '',
    tags: [''],
    keyword: '',
    description: '',
    tableHeader: ['categoryId', 'categoryName'],
    tableBody: [
      ['cat1', 'Walt Disney'],
      ['cat2', 'Picture books'],
      ['cat3', 'Bedtime stories']
    ]
  }
];
