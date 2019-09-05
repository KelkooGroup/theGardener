import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderComponent} from './header.component';
import {RouterTestingModule} from '@angular/router/testing';
import {MatSnackBarModule, MatTabsModule} from '@angular/material';
import {MenuService} from '../../../_services/menu.service';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {of} from 'rxjs';
import {MENU_HEADER_SERVICE_RESPONSE} from '../../../_testUtils/test-data.spec';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub} from '../../../_testUtils/activated-route-stub.spec';

fdescribe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let page: Page;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        HeaderComponent,
      ],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        MatTabsModule,
        MatSnackBarModule,
        BrowserAnimationsModule,
      ],
      providers: [
        MenuService,
        {
          provide: ActivatedRoute,
          useClass: ActivatedRouteStub,
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    const fakeMenuService: MenuService = TestBed.get(MenuService);
    spyOn(fakeMenuService, 'getMenuHeader').and.returnValue(of(MENU_HEADER_SERVICE_RESPONSE));

    // activatedRoute = fixture.debugElement.injector.get(ActivatedRoute) as any;
    router = TestBed.get(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
  });

  it('should show the first level of hierarchy as elements of menu with navigation', async(() => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(page.navigationItems.length).toBe(2);
    expect(page.navigationItems[0].textContent).toBe('Engineering view');
    // expect(page.navigationItems[0].getAttribute('ng-reflect-router-link')).toMatch('/app/documentation/navigate/_eng');
    expect(page.navigationItems[1].textContent).toBe('Business view');
    // expect(page.navigationItems[1].getAttribute('ng-reflect-router-link')).toMatch('/app/documentation/navigate/_biz');
  }));

});

class Page {
  constructor(private fixture: ComponentFixture<HeaderComponent>) {
  }

  get navigationItems(): Array<HTMLBaseElement> {
    return this.fixture.nativeElement.querySelectorAll('.header-navigation > a');
  }

}
