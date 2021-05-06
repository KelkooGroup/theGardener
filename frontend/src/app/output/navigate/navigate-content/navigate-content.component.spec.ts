import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute, Router} from '@angular/router';
import {of} from 'rxjs';
import {RouterTestingModule} from '@angular/router/testing';
import {NavigateContentComponent} from './navigate-content.component';
import {PageService} from '../../../_services/page.service';
import {DIRECTORIES_SERVICE_RESPONSE} from '../../../_testUtils/test-data.spec';
import {ActivatedRouteStub} from '../../../_testUtils/activated-route-stub.spec';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTabsModule } from '@angular/material/tabs';
import {PageContentComponent} from '../../page-content/page-content.component';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {RemoveHtmlSanitizerPipe} from '../../../removehtmlsanitizer.pipe';
import {InternalLinkPipe} from '../../../internal-link.pipe';
import {AnchorPipe} from '../../../anchor.pipe';
import {GherkinComponent} from '../../gherkin/gherkin.component';
import {GherkinLongTextComponent} from '../../gherkin/gherkin-long-text/gherkin-long-text.component';
import {GherkinStepComponent} from '../../gherkin/gherkin-step/gherkin-step.component';
import {SafePipe} from '../../../safe.pipe';
import {OpenApiEndPointsComponent} from '../../page-content/open-api-end-points/open-api-end-points.component';
import {OpenApiModelComponent} from '../../page-content/open-api-model/open-api-model.component';
import {MarkdownModule} from 'ngx-markdown';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {GherkinTableComponent} from '../../gherkin/gherkin-table/gherkin-table.component';
import {MatTableModule} from '@angular/material/table';
import {FooterComponent} from '../../../_components/page/footer/footer.component';
import {NavigateMobileMenuComponent} from '../navigate-mobile-menu/navigate-mobile-menu.component';
import {NavigateMenuItemComponent} from '../navigate-menu/navigate-menu-item/navigate-menu-item.component';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule} from '@angular/forms';


describe('NavigateContentComponent', () => {
    let component: NavigateContentComponent;
    let fixture: ComponentFixture<NavigateContentComponent>;
    let router: Router;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [
                NavigateContentComponent,
                PageContentComponent,
                MatProgressSpinner,
                RemoveHtmlSanitizerPipe,
                SafePipe,
                InternalLinkPipe,
                AnchorPipe,
                GherkinTableComponent,
                GherkinComponent,
                GherkinLongTextComponent,
                GherkinStepComponent,
                OpenApiEndPointsComponent,
                OpenApiModelComponent,
                FooterComponent,
                NavigateMobileMenuComponent,
                NavigateMenuItemComponent,
            ], imports: [
                HttpClientTestingModule,
                MatSnackBarModule,
                MatTabsModule,
                NoopAnimationsModule,
                MarkdownModule.forRoot(),
                NgxJsonViewerModule,
                MatTableModule,
                RouterTestingModule,
                HttpClientTestingModule,
                NoopAnimationsModule,
                RouterTestingModule,
                MatTabsModule,
                MatSnackBarModule,
                MatSelectModule,
                FormsModule,
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
        const pageService: PageService = TestBed.inject(PageService);
        spyOn(pageService, 'getRootDirectoryForPath').and.returnValue(of(DIRECTORIES_SERVICE_RESPONSE));

        router = TestBed.inject(Router);
        spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

});


