import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NavigateMenuComponent} from './navigate-menu.component';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRoute} from '@angular/router';
import {of} from 'rxjs';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule, MatIconModule, MatSelectModule} from '@angular/material';
import {RouterTestingModule} from '@angular/router/testing';
import {NavigateMenuItemComponent} from './navigate-menu-item/navigate-menu-item.component';

describe('NavigateMenuComponent', () => {
  let component: NavigateMenuComponent;
  let fixture: ComponentFixture<NavigateMenuComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        NavigateMenuComponent,
        NavigateMenuItemComponent,
      ],
      imports: [
        MatFormFieldModule,
        MatSelectModule,
        MatIconModule,
        NoopAnimationsModule,
        HttpClientTestingModule,
        RouterTestingModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({name: 'eng'}),
          }
        },
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigateMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

});
