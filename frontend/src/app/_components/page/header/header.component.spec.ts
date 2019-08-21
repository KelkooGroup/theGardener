import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderComponent} from './header.component';
import {RouterTestingModule} from '@angular/router/testing';
import {MatSnackBarModule, MatTabsModule} from '@angular/material';
import {MenuService} from '../../../_services/menu.service';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {of} from 'rxjs';
import {MENU_SERVICE_RESPONSE} from '../../../test/test-data.spec';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let page: Page;

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
        MenuService
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);

    const fakeMenuService: MenuService = TestBed.get(MenuService);
    spyOn(fakeMenuService, 'hierarchy').and.returnValue(of(MENU_SERVICE_RESPONSE));
    fixture.detectChanges();
  });

  it('should show the first level of hierarchy as elements of menu with navigation', async(() => {
    expect(component).toBeTruthy();
    expect(page.navigationItems.length).toBe(2);
    expect(page.navigationItems[0].textContent).toBe('Engineering view');
    expect(page.navigationItems[0].href).toMatch('/app/documentation/navigate/_eng');
    expect(page.navigationItems[1].textContent).toBe('Business view');
    expect(page.navigationItems[1].href).toMatch('/app/documentation/navigate/_biz');
  }));
});

class Page {
  constructor(private fixture: ComponentFixture<HeaderComponent>) {}

  get navigationItems(): Array<HTMLBaseElement> {
    return this.fixture.nativeElement.querySelectorAll('.header-navigation > a');
  }

}
