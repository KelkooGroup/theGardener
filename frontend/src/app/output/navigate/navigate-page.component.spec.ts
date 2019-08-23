import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NavigatePageComponent} from './navigate-page.component';
import {NavigateContentComponent} from './navigate-content/navigate-content.component';
import {NavigateMenuComponent} from './navigate-menu/navigate-menu.component';
import {
  MatFormFieldModule,
  MatIconModule,
  MatProgressSpinnerModule,
  MatSelectModule,
  MatSidenavModule,
  MatTabsModule
} from '@angular/material';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';
import {RouterTestingModule} from '@angular/router/testing';
import {NavigateMenuItemComponent} from './navigate-menu/navigate-menu-item/navigate-menu-item.component';
import {FormsModule} from '@angular/forms';


describe('NavigatePageComponent', () => {
  let component: NavigatePageComponent;
  let fixture: ComponentFixture<NavigatePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        NavigatePageComponent,
        NavigateContentComponent,
        NavigateMenuComponent,
        NavigateMenuItemComponent,
      ], imports: [
        MatSidenavModule,
        MatFormFieldModule,
        MatSelectModule,
        MatIconModule,
        MatTabsModule,
        MatProgressSpinnerModule,
        FormsModule,
        HttpClientTestingModule,
        NoopAnimationsModule,
        RouterTestingModule,
      ], providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({name: 'eng'}),
          }
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigatePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
