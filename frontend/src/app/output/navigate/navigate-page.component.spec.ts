import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {NavigatePageComponent} from './navigate-page.component';
import {NavigateContentComponent} from './navigate-content.component';
import {NavigateMenuComponent} from './navigate-menu/navigate-menu.component';
import {GherkinComponent} from '../gherkin/gherkin.component';
import {MatListModule} from '@angular/material/list';
import {GherkinBackgroundComponent} from '../gherkin/gherkin-background/gherkin-background.component';
import {GherkinFeatureComponent} from '../gherkin/gherkin-feature/gherkin-feature.component';
import {GherkinStepComponent} from '../gherkin/gherkin-step/gherkin-step.component';
import {GherkinTableComponent} from '../gherkin/gherkin-table/gherkin-table.component';
import {GherkinLongTextComponent} from '../gherkin/gherkin-long-text/gherkin-long-text.component';
import {
  MatButtonModule,
  MatCardModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDialogModule,
  MatDividerModule, MatExpansionModule,
  MatFormFieldModule, MatIconModule,
  MatInputModule, MatProgressBarModule, MatProgressSpinnerModule,
  MatRadioModule,
  MatSelectModule, MatSnackBarModule,
  MatTableModule,
  MatTabsModule,
  MatTreeModule
} from '@angular/material';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {AppRoutingModule} from '../../app-routing.module';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";


describe('NavigatePageComponent', () => {
  let component: NavigatePageComponent;
  let fixture: ComponentFixture<NavigatePageComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        NavigatePageComponent,
        NavigateContentComponent,
        NavigateMenuComponent,
        GherkinComponent,
        GherkinBackgroundComponent,
        GherkinFeatureComponent,
        GherkinStepComponent,
        GherkinTableComponent,
        GherkinLongTextComponent,
      ], imports: [
        MatCardModule,
        MatButtonModule,
        MatCheckboxModule,
        MatChipsModule,
        MatDialogModule,
        MatDividerModule,
        MatFormFieldModule,
        MatInputModule,
        MatListModule,
        MatTableModule,
        MatSelectModule,
        MatRadioModule,
        MatTabsModule,
        MatTreeModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatExpansionModule,
        MatProgressBarModule,
        MatSnackBarModule,
        NgxJsonViewerModule,
        HttpClientTestingModule,
        AppRoutingModule,
        BrowserAnimationsModule,
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigatePageComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.get(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    fixture.whenStable().then(() => {
      const req = httpMock.expectOne('api/menu');
      expect(req.request.method).toBe('GET');
    })
  });

});
