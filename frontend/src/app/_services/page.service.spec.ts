import {async, TestBed} from '@angular/core/testing';

import {PageService} from './page.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {DirectoryApi, PageApi} from '../_models/hierarchy';

describe('PageService', () => {
  let httpMock: HttpTestingController;
  let pageService: PageService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PageService,
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    httpMock = TestBed.get(HttpTestingController);
    pageService = TestBed.get(PageService);
    expect(pageService).toBeTruthy();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should get list of pages for path', async(() => {
    pageService.getDirectoriesForPath('publisherManagementWS>qa>/constraints/')
      .subscribe(directories => {
        expect(directories).toBeDefined();
        expect(directories.length).toBe(1);
        expect(directories[0].pages.length).toBe(4);
        expect(directories[0].pages[0].name).toEqual('overview');
        expect(directories[0].pages[0].order).toBe(0);
        expect(directories[0].pages[1].name).toEqual('for_a_publisher');
        expect(directories[0].pages[1].order).toBe(1);
        expect(directories[0].pages[2].name).toEqual('for_a_merchant');
        expect(directories[0].pages[2].order).toBe(2);
        expect(directories[0].pages[3].name).toEqual('for_an_offer');
        expect(directories[0].pages[3].order).toBe(3);
      });


    const req = httpMock.expectOne(req =>
      req.method === 'GET' && req.url === 'api/directories' && req.params.get('path') === 'publisherManagementWS>qa>/constraints/');
    req.flush(DIRECTORIES_SERVICE_RESPONSE);
  }));

  it('should get page for path', async(() => {
    pageService.getPage('publisherManagementWS>qa>/constraints/overview')
      .subscribe(pages => {
        expect(pages).toBeDefined();
        expect(pages.length).toBe(1);
        expect(pages[0].path).toEqual('publisherManagementWS>qa>/constraints/overview');
        expect(pages[0].order).toBe(0);
        expect(pages[0].markdown).toBeDefined();
      });

    const req = httpMock.expectOne(req =>
      req.method === 'GET' && req.url === 'api/pages' && req.params.get('path') === 'publisherManagementWS>qa>/constraints/overview');
    req.flush(PAGE_SERVICE_RESPONSE);
  }));
});

const DIRECTORIES_SERVICE_RESPONSE: Array<DirectoryApi> = [
  {
    id: '11',
    path: 'publisherManagementWS>qa>/constraints/',
    name: 'constraints',
    label: 'Constraints',
    description: 'Filter offers provided to the publisher',
    order: 0,
    pages: [
      {
        path: 'publisherManagementWS>qa>/constraints/overview',
        relativePath: '/constraints/overview',
        name: 'overview',
        label: 'overview',
        description: 'overview',
        order: 0,
      },
      {
        path: 'publisherManagementWS>qa>/constraints/for_a_publisher',
        relativePath: '/constraints/for_a_publisher',
        name: 'for_a_publisher',
        label: 'for_a_publisher',
        description: 'for_a_publisher',
        order: 1,
      },
      {
        path: 'publisherManagementWS>qa>/constraints/for_a_merchant',
        relativePath: '/constraints/for_a_merchant',
        name: 'for_a_merchant',
        label: 'for_a_merchant',
        description: 'for_a_merchant',
        order: 2,
      },
      {
        path: 'publisherManagementWS>qa>/constraints/for_an_offer',
        relativePath: '/constraints/for_an_offer',
        name: 'for_an_offer',
        label: 'for_an_offer',
        description: 'for_an_offer',
        order: 3,
      }
    ]
  }
];

const PAGE_SERVICE_RESPONSE: Array<PageApi> = [{
  path: 'publisherManagementWS>qa>/constraints/overview',
  relativePath: '/constraints/overview',
  name: 'overview',
  label: 'overview',
  description: 'overview',
  order: 0,
  markdown: '```thegardener { "page" : { "label": "Constraints", "description": "Filter offers provided to the publisher" } } ``` For various reasons, the offers provided to the publishers can be filtered. We don\'t necessary want to provide all the offers to the publishers : ![Overview](../assets/images/constraints_overview.png) This is the constraints objective. The constraints can be defined manually by the **BizDevs** or automatically by the **TrafficOptimizer**. ### Reference and application The constraints are defined and stored in PMWS component. The impact of those constraints is implemented at the client level: - **eCS** and **ShoppingAPI** are filtering the call to Search6 - **FeedService** is filtering the offers provided (offers coming from OfferProcessing) - others systems are also using the constraints to filter offers provided to external clients : example GSA Exporter or COP. The constraints are stored against a profile (which is linked to a contract, which is linked to the publisher itself ([See details](thegardener://${current.project}/${current.branch}/overview))). All trackings of a profile share the same constraints. Clients are most of the time using a tracking as input data to find out what are the constraints to be applied. ### Different sources of constraints There are several ways to define the constraints : - for a publisher, [filter the merchants that can provide offers](thegardener://${current.project}/${current.branch}/constraints/for_a_publisher). - for a merchant, [filter the publishers that can receive offers](thegardener://${current.project}/${current.branch}/constraints/for_a_merchant). - moreover, [offers can be filtered for various reasons](thegardener://${current.project}/${current.branch}/constraints/for_an_offer). ![Sources](../assets/images/constraints_sources_overview.png) All those filters are cumulative : **each offer need to pass through all the filters**. In other words it\'s a AND between each constraint. We can see the impact of [those constraints on the PMBO](thegardener://${current.project}/${current.branch}/constraints/from_pmbo). '
}];
