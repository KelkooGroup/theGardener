import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {GherkinTableComponent} from './gherkin-table.component';
import {MatTableModule} from '@angular/material';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';

describe('GherkinTableComponent', () => {
  let component: GherkinTableComponent;
  let fixture: ComponentFixture<GherkinTableComponent>;
  let page: Page;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GherkinTableComponent,
      ], imports: [
        MatTableModule,
        NoopAnimationsModule,
      ]
    })
      .compileComponents();
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
    return  this.fixture.nativeElement.querySelector('.mat-table');
  }

  get tableHeader(): Array<string> {
    const res: Array<string> = [];
    this.fixture.nativeElement
      .querySelectorAll('.mat-header-cell')
      .forEach((h: HTMLElement) => res.push(h.textContent.trim()));
    return res;
  }

  get tableRows(): Array<HTMLElement> {
    return this.fixture.nativeElement
      .querySelectorAll('.mat-row');
  }

  getTableRow(index: number): Array<string> {
    const rows: Array<HTMLElement> = this.tableRows;
    expect(rows.length > index);
    const res: Array<string> = [];
    rows[index]
      .querySelectorAll('.mat-cell')
      .forEach(element => res.push(element.textContent.trim()));
    return res;
  }
}

const TABLE: Array<Array<string>> = [
  ['categoryId', 'categoryName'],
  ['cat1', 'Walt Disney'],
  ['cat2', 'Picture books'],
  ['cat3', 'Bedtime stories']
];

const EXAMPLES = [{
  id: "",
  tags: [""],
  keyword: "",
  description: "",
  tableHeader: ["categoryId", "categoryName"],
  tableBody: [
    ["cat1", "Walt Disney"],
    ["cat2", "Picture books"],
    ["cat3", "Bedtime stories"]
  ]
}];
