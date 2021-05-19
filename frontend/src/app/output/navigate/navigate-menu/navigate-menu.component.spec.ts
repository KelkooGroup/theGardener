import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NavigateMenuComponent } from './navigate-menu.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';
import { NavigateMenuItemComponent } from './navigate-menu-item/navigate-menu-item.component';
import { FormsModule } from '@angular/forms';

describe('NavigateMenuComponent', () => {
  let component: NavigateMenuComponent;
  let fixture: ComponentFixture<NavigateMenuComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [NavigateMenuComponent, NavigateMenuItemComponent],
        imports: [
          MatFormFieldModule,
          MatSelectModule,
          MatIconModule,
          MatProgressSpinnerModule,
          MatSnackBarModule,
          FormsModule,
          NoopAnimationsModule,
          HttpClientTestingModule,
          RouterTestingModule
        ],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              params: of({ name: 'eng' })
            }
          }
        ]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigateMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
