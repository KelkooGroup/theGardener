import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PageContentComponent, PageContentComponentTools} from './page-content.component';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRouteStub} from '../../_testUtils/activated-route-stub.spec';
import {PageService} from '../../_services/page.service';
import {of} from 'rxjs';
import {MarkdownModule} from 'ngx-markdown';
import {Page, PagePart} from '../../_models/page';
import {MatProgressSpinnerModule, MatSnackBarModule, MatTableModule} from '@angular/material';
import {SafePipe} from '../../safe.pipe';
import {InternalLinkPipe} from '../../internal-link.pipe';
import {GherkinComponent} from '../gherkin/gherkin.component';
import {GherkinStepComponent} from '../gherkin/gherkin-step/gherkin-step.component';
import {GherkinLongTextComponent} from '../gherkin/gherkin-long-text/gherkin-long-text.component';
import {GherkinTableComponent} from '../gherkin/gherkin-table/gherkin-table.component';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {RemoveHtmlSanitizerPipe} from '../../removehtmlsanitizer.pipe';
import {RouterTestingModule} from '@angular/router/testing';
import {AnchorPipe} from '../../anchor.pipe';

describe('PageContentComponent', () => {
  let component: PageContentComponent;
  let fixture: ComponentFixture<PageContentComponent>;
  let page: PageObject;
  let activatedRoute: ActivatedRouteStub;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PageContentComponent,
        GherkinComponent,
        GherkinStepComponent,
        GherkinLongTextComponent,
        GherkinTableComponent,
        SafePipe,
        InternalLinkPipe,
        AnchorPipe,
        RemoveHtmlSanitizerPipe
      ],
      imports: [
        HttpClientTestingModule,
        MatProgressSpinnerModule,
        MatSnackBarModule,
        MatTableModule,
        MarkdownModule.forRoot(),
        NgxJsonViewerModule,
        RouterTestingModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useClass: ActivatedRouteStub,
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PageContentComponent);
    component = fixture.componentInstance;
    page = new PageObject(fixture);
    activatedRoute = fixture.debugElement.injector.get(ActivatedRoute) as any;

    activatedRoute.testParentParams = {name: '_eng', path: 'suggestionsWS>qa>_pmws_'};
    activatedRoute.testParams = {page: 'overview'};
    activatedRoute.fragment = of('');
  });

  it('should get page content from backend and show markdown', async(() => {
    const pageService: PageService = TestBed.get(PageService);
    spyOn(pageService, 'getPage').and.returnValue(of(PAGE_SERVICE_RESPONSE));

    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(pageService.getPage).toHaveBeenCalledWith('suggestionsWS>qa>_pmws_overview');

    expect(page.title).toMatch('overview');
    expect(page.pageContent.startsWith('For various reasons'))
      .withContext(`Page content should start with "For various reasons" but was ${fixture.nativeElement.textContent}`)
      .toBeTruthy();
  }));

  it('should get page content from backend and show markdown', async(() => {
    const pageService: PageService = TestBed.get(PageService);
    spyOn(pageService, 'getPage').and.returnValue(of(PAGE_WITH_INTERNAL_LINK_SERVICE_RESPONSE));

    fixture.detectChanges();
    expect(component).toBeTruthy();

  }));

  it('should show an iframe if page is an external link', async(() => {
    const pageService: PageService = TestBed.get(PageService);
    spyOn(pageService, 'getPage').and.returnValue(of(PAGE_WITH_EXTERNAL_LINK_RESPONSE));

    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(page.iframe).toBeTruthy();
    expect(page.iframe.src).toEqual('http://publisher.corp.kelkoo.net/docs/#/Contact%20Management/getContact');
  }));

  it('should show scenario settings if page contains a scenario', async(() => {
    const pageService: PageService = TestBed.get(PageService);
    spyOn(pageService, 'getPage').and.returnValue(of(PAGE_WITH_SCENARIO));

    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(page.iframe).toBeFalsy();
    expect(page.scenario).toBeTruthy();
  }));

  it('should navigate to the right url when click on internal link', async(() => {
    const path = 'app/documentation/navigate/HierarchyNode;path=theGardener>master>_features_/administration';

    const router: Router = TestBed.get(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    PageContentComponentTools.navigate(router, path);

    expect(router.navigate).toHaveBeenCalledWith(['app/documentation/navigate/HierarchyNode', {path: 'theGardener>master>_features_'}, 'administration'], {fragment: undefined})
  }));

  it('should not crash when bad path in internal link', async(() => {
    const path: string = null;
    const path2 = '';
    const path3 = ' ';
    const path4 = ';';
    const router: Router = TestBed.get(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    PageContentComponentTools.navigate(router, path);
    expect(router.navigate).not.toHaveBeenCalled();

    PageContentComponentTools.navigate(router, path2);
    expect(router.navigate).not.toHaveBeenCalled();

    PageContentComponentTools.navigate(router, path3);
    expect(router.navigate).not.toHaveBeenCalled();

    PageContentComponentTools.navigate(router, path4);
    expect(router.navigate).not.toHaveBeenCalled();
  }));
});

class PageObject {
  constructor(private fixture: ComponentFixture<PageContentComponent>) {
  }

  get title(): string {
    const title = this.fixture.nativeElement.querySelector('h1');
    expect(title).toBeTruthy();
    return title.textContent;
  }

  get pageContent(): string {
    const pageContent = this.fixture.nativeElement.querySelector('.markdown');
    expect(pageContent).toBeTruthy();
    return pageContent.textContent;
  }

  get iframe() {
    return this.fixture.nativeElement.querySelector('iframe');
  }

  get scenario() {
    return this.fixture.nativeElement.querySelector('.scenario');
  }
}

const PAGE_MARKDOWN: PagePart = {
  type: 'markdown',
  data: {
    // tslint:disable-next-line:max-line-length
    markdown: 'For various reasons, the offers provided to the publishers can be filtered. We don\'t necessary want to provide all the offers to the publishers : ![Overview](../assets/images/constraints_overview.png) This is the constraints objective. The constraints can be defined manually by the **BizDevs** or automatically by the **TrafficOptimizer**. ### Reference and application The constraints are defined and stored in PMWS component. The impact of those constraints is implemented at the client level: - **eCS** and **ShoppingAPI** are filtering the call to Search6 - **FeedService** is filtering the offers provided (offers coming from OfferProcessing) - others systems are also using the constraints to filter offers provided to external clients : example GSA Exporter or COP. The constraints are stored against a profile (which is linked to a contract, which is linked to the publisher itself ([See details](thegardener://${current.project}/${current.branch}/overview))). All trackings of a profile share the same constraints. Clients are most of the time using a tracking as input data to find out what are the constraints to be applied. ### Different sources of constraints There are several ways to define the constraints : - for a publisher, [filter the merchants that can provide offers](thegardener://${current.project}/${current.branch}/constraints/for_a_publisher). - for a merchant, [filter the publishers that can receive offers](thegardener://${current.project}/${current.branch}/constraints/for_a_merchant). - moreover, [offers can be filtered for various reasons](thegardener://${current.project}/${current.branch}/constraints/for_an_offer). ![Sources](../assets/images/constraints_sources_overview.png) All those filters are cumulative : **each offer need to pass through all the filters**. In other words it\'s a AND between each constraint. We can see the impact of [those constraints on the PMBO](thegardener://${current.project}/${current.branch}/constraints/from_pmbo). '
  }
};

const PAGE_MARKDOWN_WITH_INTERNAL_LINK: PagePart = {
  type: 'markdown',
  data: {
    // tslint:disable-next-line:max-line-length
    markdown: 'Internal link <a href="thegardener://path=theGardener>master>_features_/administration">sdfg</a>'
  }
};

const PAGE_SERVICE_RESPONSE: Page = {
  title: 'overview',
  order: 0,
  path: '',
  parts: [
    PAGE_MARKDOWN,
  ],
};

const PAGE_WITH_INTERNAL_LINK_SERVICE_RESPONSE: Page = {
  title: 'overview',
  order: 0,
  path: '',
  parts: [
    PAGE_MARKDOWN_WITH_INTERNAL_LINK,
  ],
};

const EXTERNAL_LINK_PART: PagePart = {
  type: 'includeExternalPage',
  data: {
    includeExternalPage: 'http://publisher.corp.kelkoo.net/docs/#/Contact%20Management/getContact'
  }
};

const PAGE_WITH_EXTERNAL_LINK_RESPONSE: Page = {
  title: 'overview',
  path: '',
  order: 0,
  parts: [EXTERNAL_LINK_PART],
};

const SCENARIO_PART: PagePart = {
  type: 'scenarios',
  data: {
    scenarios: {
      id: '692',
      branchId: '44',
      path: 'test/features/register_projects/register_a_project.feature',
      background: {
        id: '0',
        keyword: 'Background',
        name: '',
        description: '',
        steps: [
          {
            id: '0',
            keyword: 'Given',
            text: 'the database is empty',
            argument: []
          },
          {
            id: '1',
            keyword: 'And',
            text: 'the cache is empty',
            argument: []
          }
        ]
      },
      tags: [],
      language: 'en',
      keyword: 'Feature',
      name: 'Register a project',
      description: 'As a user,\n  I want to register my project into theGardener\n  So that my project BDD features will be shared with all users',
      scenarios: [
        {
          keyword: 'Scenario',
          name: 'get a project',
          description: '',
          tags: [
            'level_2_technical_details',
            'nominal_case',
            'valid'
          ],
          abstractionLevel: 'level_2_technical_details',
          id: '4968',
          caseType: 'nominal_case',
          steps: [
            {
              id: '0',
              keyword: 'Given',
              text: 'we have the following projects',
              argument: [
                [
                  'id',
                  'name',
                  'repositoryUrl',
                  'stableBranch',
                  'featuresRootPath'
                ],
                [
                  'suggestionsWS',
                  'Suggestions WebServices',
                  'git@gitlab.corp.kelkoo.net:library/suggestionsWS.git',
                  'master',
                  'test/features'
                ]
              ]
            },
            {
              id: '1',
              keyword: 'When',
              text: 'I perform a \"GET\" on following URL \"/api/projects/suggestionsWS\"',
              argument: []
            },
            {
              id: '2',
              keyword: 'Then',
              text: 'I get a response with status \"200\"',
              argument: []
            },
            {
              id: '3',
              keyword: 'And',
              text: 'I get the following json response body',
              argument: [
                [
                  '{\n  \"id\": \"suggestionsWS\",\n  \"name\": \"Suggestions WebServices\",\n  \"repositoryUrl\": \"git@gitlab.corp.kelkoo.net:library/suggestionsWS.git\",\n  \"stableBranch\": \"master\",\n  \"featuresRootPath\": \"test/features\"\n}'
                ]
              ]
            }
          ],
          workflowStep: 'valid'
        }
      ],
      comments: []
    }
  }
};

const PAGE_WITH_SCENARIO: Page = {
  title: 'feature',
  path: '',
  order: 0,
  parts: [PAGE_MARKDOWN, SCENARIO_PART],
};
