import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NavigateMenuItemComponent } from './navigate-menu-item.component';
import {MatFormFieldModule, MatIconModule, MatSelectModule} from '@angular/material';
import {RouterTestingModule} from '@angular/router/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';

describe('NavigateMenuItemComponent', () => {
  let component: NavigateMenuItemComponent;
  let fixture: ComponentFixture<NavigateMenuItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NavigateMenuItemComponent ],
      imports: [
        MatIconModule,
        MatFormFieldModule,
        MatSelectModule,
        RouterTestingModule,
        NoopAnimationsModule,
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigateMenuItemComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    component.menuItem = {
      name: 'suggestion',
      label: 'Suggestion system',
      type: 'Node',
      depth: 1,
      children: []
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
