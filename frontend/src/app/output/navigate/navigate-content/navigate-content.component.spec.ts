import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute, Router} from '@angular/router';
import {of} from 'rxjs';
import {RouterTestingModule} from '@angular/router/testing';
import {NavigateContentComponent} from './navigate-content.component';
import {PageService} from '../../../_services/page.service';
import {DIRECTORIES_SERVICE_RESPONSE} from '../../../test/test-data.spec';
import {ActivatedRouteStub} from '../../../test/activated-route-stub.spec';
import {MatSnackBarModule, MatTabsModule} from '@angular/material';


describe('NavigateContentComponent', () => {
  let component: NavigateContentComponent;
  let fixture: ComponentFixture<NavigateContentComponent>;
  let activatedRoute: ActivatedRouteStub;
  let router: Router;
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
        MatSnackBarModule,
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
    page = new Page(fixture);
    const pageService: PageService = TestBed.get(PageService);
    spyOn(pageService, 'getRootDirectoryForPath').and.returnValue(of(DIRECTORIES_SERVICE_RESPONSE));

    router = TestBed.get(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    activatedRoute = fixture.debugElement.injector.get(ActivatedRoute) as any;
  });

  afterAll(() => {
    fixture.destroy();
  });

  it('should show tabs in the right order', () => {
    activatedRoute.testParams = {path: 'constraints/'};
    activatedRoute.testChildParams = {};

    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(page.tabs).toBeTruthy();
    expect(page.tabs.length).toBe(4);
    expect(page.tabs[0].textContent).toMatch('Overview');
    expect(page.tabs[1].textContent).toMatch('For a publisher');
    expect(page.tabs[2].textContent).toMatch('For an offer');
    expect(page.tabs[3].textContent).toMatch('For a merchant');
  });

  it('should navigate to item when clicking on a tab', () => {
    activatedRoute.testParams = {path: 'constraints/'};
    activatedRoute.testChildParams = {};

    fixture.detectChanges();
    const href = page.tabs[2].getAttribute('ng-reflect-router-link');
    expect(href).toEqual('for_an_offer');
  });

  it('should redirect to first child page if route was on directory', async(() => {
    activatedRoute.testParams = {path: 'constraints/'};
    activatedRoute.testChildParams = {};

    fixture.detectChanges();
    fixture.whenStable().then(() => {
      expect(router.navigate).toHaveBeenCalledWith(['overview'], {relativeTo: activatedRoute});
    });
  }));

  it('should not redirect if child page is already defined in route', async(() => {
    activatedRoute.testParams = {path: 'constraints/'};
    activatedRoute.testChildParams = {page: 'for_a_merchant'};
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(router.navigate).not.toHaveBeenCalled();
    });
  }));
});

class Page {
  constructor(private fixture: ComponentFixture<NavigateContentComponent>) {
  }

  get tabs() {
    return this.fixture.nativeElement.querySelectorAll('.mat-tab-link');
  }
}
