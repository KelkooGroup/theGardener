import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {GherkinLongTextComponent} from './gherkin-long-text.component';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import { MatTabsModule } from '@angular/material/tabs';


describe('GherkinLongTextComponent', () => {
  let component: GherkinLongTextComponent;
  let fixture: ComponentFixture<GherkinLongTextComponent>;
  let page: Page;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [
        GherkinLongTextComponent,
      ], imports: [
        NgxJsonViewerModule,
        NoopAnimationsModule,
        MatTabsModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GherkinLongTextComponent);
    page = new Page(fixture);
  });

  it('should show simple step', () => {
    component = fixture.componentInstance;
    component.longText = 'This is a long text non json that must be displayed';
    fixture.detectChanges();

    expect(page.json).toBeFalsy();
    expect(page.longText).toBeTruthy();
    expect(page.longText.textContent.trim()).toBe('This is a long text non json that must be displayed');
  });

  it('should show simple step with contentType', () => {
    component = fixture.componentInstance;
    component.longText = '<xml>This is a long text</xml>';
    component.longTextType = 'xml';
    fixture.detectChanges();

    expect(page.json).toBeFalsy();
    expect(page.longText).toBeTruthy();
    expect(page.longText.textContent.trim()).toBe('<xml>This is a long text</xml>');
  });

  it('should show json', () => {
    component = fixture.componentInstance;
    component.longText = '{"text": "blablabla"}';
    fixture.detectChanges();

    expect(page.longText).toBeFalsy();
    expect(page.json).toBeTruthy();
    expect(page.json.textContent.trim()).toEqual('text: "blablabla"');
  });
});

class Page {
  constructor(private fixture: ComponentFixture<GherkinLongTextComponent>) {}

  get json(): HTMLElement {
    return this.fixture.nativeElement.querySelector('ngx-json-viewer');
  }

  get longText(): HTMLElement {
    return this.fixture.nativeElement.querySelector('.long-text');
  }
}
