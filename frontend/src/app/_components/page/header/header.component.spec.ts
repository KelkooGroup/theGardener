import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {HeaderComponent} from './header.component';
import {RouterTestingModule} from "@angular/router/testing";
import {NavigationItem} from "../../../_models/navigation";
import {MatSnackBarModule, MatTabsModule} from "@angular/material";
import {MenuService} from "../../../_services/menu.service";
import {HttpClientModule} from "@angular/common/http";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  const items: Array<NavigationItem> = [{
    route: '_test',
    displayName: 'test',
    itemOptionPlaceHolder: '',
    itemOptions: true,
    itemType: '',
    selected: false,
    toBeDisplayed: false,
    itemChildren(): Array<NavigationItem> {
      return null;
    },
    itemOptionSelected(): NavigationItem {
      return null;
    },
    matchPage(page: string): boolean {
      return true;
    },
  }];

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
    component.items = items;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
