import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderComponent} from './header.component';
import {RouterTestingModule} from '@angular/router/testing';
import {MatSnackBarModule, MatTabsModule} from '@angular/material';
import {MenuService} from '../../../_services/menu.service';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {of} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub} from '../../../_testUtils/activated-route-stub.spec';
import {MenuHierarchy, MenuType} from "../../../_models/menu";
import {NavigateMobileMenuComponent} from '../../../output/navigate/navigate-mobile-menu/navigate-mobile-menu.component';
import {NavigateMenuItemComponent} from '../../../output/navigate/navigate-menu/navigate-menu-item/navigate-menu-item.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {FormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {SearchQueryComponent} from "../../../output/search/search-query/search-query.component";

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRouteStub;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        HeaderComponent,
        NavigateMobileMenuComponent,
        NavigateMenuItemComponent,
        SearchQueryComponent
      ],
      imports: [
        MatTabsModule,
        MatSnackBarModule,
        MatFormFieldModule,
        MatSelectModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        FormsModule,
        NoopAnimationsModule,
        HttpClientTestingModule,
        RouterTestingModule,
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

    activatedRoute = fixture.debugElement.injector.get(ActivatedRoute) as any;
    activatedRoute.testUrl = [];
    router = TestBed.get(Router);
    spyOn(router, 'navigateByUrl').and.returnValue(Promise.resolve(true));
  });

  /*
    In this test, we need to rely on ng-reflect-router-link as href value is `/` or `localhost:9876` when using fake ActivatedRoute
   */
  it('should show the first level of hierarchy as elements of menu with navigation', async(() => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(page.navigationItems.length).toBe(2);
    expect(page.navigationItems[0].textContent).toBe('Publisher');
    // ng-reflect---- properties are for debugging / devtime only, and they're truncated so they don't dump huge amounts of data into the DOM.
    expect(page.navigationItems[1].textContent).toBe('Tools');
  }));

  it('should navigate to first element if no route is set', async(() => {
    activatedRoute.testParams = {};
    fixture.detectChanges();

    expect(router.navigateByUrl).toHaveBeenCalledWith('app/documentation/navigate/_publisher/_/_/_');
  }));


});

class Page {
  constructor(private fixture: ComponentFixture<HeaderComponent>) {
  }

  get navigationItems(): Array<HTMLBaseElement> {
    return this.fixture.nativeElement.querySelectorAll('.header-navigation > a');
  }

}

export const MENU_HEADER_SERVICE_RESPONSE: Array<MenuHierarchy> =  [
      {
        name: 'publisher',
        label: 'Publisher',
        type: 'Node' as MenuType,
        depth: 1,
        route: {nodes: ['publisher'], directories:[] as  Array<string>},
        children: [] as Array<MenuHierarchy>
      },
      {
        name: 'tools',
        label: 'Tools',
        type: 'Node' as MenuType,
        depth: 1,
        route: {nodes: ['tools'], directories:[] as  Array<string>},
        children: [] as Array<MenuHierarchy>
      }
    ]
;
