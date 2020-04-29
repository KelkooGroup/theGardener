import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute, Router} from '@angular/router';
import {of} from 'rxjs';
import {RouterTestingModule} from '@angular/router/testing';
import {NavigateContentComponent} from './navigate-content.component';
import {PageService} from '../../../_services/page.service';
import {DIRECTORIES_SERVICE_RESPONSE} from '../../../_testUtils/test-data.spec';
import {ActivatedRouteStub} from '../../../_testUtils/activated-route-stub.spec';
import {MatSnackBarModule, MatTabsModule} from '@angular/material';
import {PageContentComponent} from "../../page-content/page-content.component";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {RemoveHtmlSanitizerPipe} from "../../../removehtmlsanitizer.pipe";
import {InternalLinkPipe} from "../../../internal-link.pipe";
import {AnchorPipe} from "../../../anchor.pipe";
import {GherkinComponent} from "../../gherkin/gherkin.component";
import {GherkinLongTextComponent} from "../../gherkin/gherkin-long-text/gherkin-long-text.component";
import {GherkinStepComponent} from "../../gherkin/gherkin-step/gherkin-step.component";
import {SafePipe} from "../../../safe.pipe";
import {OpenApiEndPointsComponent} from "../../page-content/open-api-end-points/open-api-end-points.component";
import {OpenApiModelComponent} from "../../page-content/open-api-model/open-api-model.component";
import {MarkdownModule} from "ngx-markdown";
import {NgxJsonViewerModule} from "ngx-json-viewer";
import {GherkinTableComponent} from "../../gherkin/gherkin-table/gherkin-table.component";
import {MatTableModule} from "@angular/material/table";


describe('NavigateContentComponent', () => {
    let component: NavigateContentComponent;
    let fixture: ComponentFixture<NavigateContentComponent>;
    let router: Router;

    beforeEach(async(() => {
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
        const pageService: PageService = TestBed.get(PageService);
        spyOn(pageService, 'getRootDirectoryForPath').and.returnValue(of(DIRECTORIES_SERVICE_RESPONSE));

        router = TestBed.get(Router);
        spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

});


