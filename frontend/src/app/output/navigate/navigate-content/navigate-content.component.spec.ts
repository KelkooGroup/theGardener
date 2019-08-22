import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';
import {RouterTestingModule} from '@angular/router/testing';
import {NavigateContentComponent} from './navigate-content.component';
import {MatTabsModule} from '@angular/material';
import {PageService} from '../../../_services/page.service';
import {DIRECTORIES_SERVICE_RESPONSE} from '../../../test/test-data.spec';
import {ActivatedRouteStub} from '../../../test/activated-route-stub.spec';


describe('NavigateContentComponent', () => {
  let component: NavigateContentComponent;
  let fixture: ComponentFixture<NavigateContentComponent>;
  let activatedRoute: ActivatedRouteStub;
  let page: Page;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        NavigateContentComponent,
      ], imports: [
        HttpClientTestingModule,
        NoopAnimationsModule,
        RouterTestingModule,
        MatTabsModule,
      ], providers: [
        {
          provide: ActivatedRoute,
          useClass: ActivatedRouteStub,
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigateContentComponent);
    component = fixture.componentInstance;
    activatedRoute = fixture.debugElement.injector.get(ActivatedRoute) as any;
    activatedRoute.testParams = {};

    page = new Page(fixture);
    let pageService : PageService = TestBed.get(PageService);
    spyOn(pageService, 'getDirectoriesForPath').and.returnValue(of(DIRECTORIES_SERVICE_RESPONSE));
    fixture.detectChanges();
  });

  it('should show tabs in the right order', () => {
    expect(component).toBeTruthy();
    expect(page.tabs).toBeTruthy();
    expect(page.tabs.length).toBe(4);
    expect(page.tabs[0].textContent).toMatch('Overview');
    expect(page.tabs[1].textContent).toMatch('For a publisher');
    expect(page.tabs[2].textContent).toMatch('For an offer');
    expect(page.tabs[3].textContent).toMatch('For a merchant');
  });

  it('should navigate to item when clicking on a tab', () => {
    const href = page.tabs[2].getAttribute('ng-reflect-router-link');
    expect(href).toEqual('for_an_offer');  });
});

class Page {
  constructor(private fixture: ComponentFixture<NavigateContentComponent>) {
  }

  get tabs() {
    return this.fixture.nativeElement.querySelectorAll('.mat-tab-link');
  }
}
