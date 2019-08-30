import {async, TestBed} from '@angular/core/testing';

import {PageService} from './page.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {
  DIRECTORIES_SERVICE_RESPONSE,
  PAGE_SERVICE_RESPONSE,
  PAGE_WITH_EXTERNAL_LINK_SERVICE_RESPONSE
} from '../_testUtils/test-data.spec';

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
    pageService.getRootDirectoryForPath('publisherManagementWS>qa>/constraints/')
      .subscribe(directory => {
        expect(directory).toBeDefined();
        expect(directory.pages.length).toBe(4);
        expect(directory.pages[0].name).toEqual('overview');
        expect(directory.pages[0].order).toBe(0);
        expect(directory.pages[1].name).toEqual('for_a_publisher');
        expect(directory.pages[1].order).toBe(1);
        expect(directory.pages[2].name).toEqual('for_a_merchant');
        expect(directory.pages[2].order).toBe(3);
        expect(directory.pages[3].name).toEqual('for_an_offer');
        expect(directory.pages[3].order).toBe(2);
      });


    const req = httpMock.expectOne(mockRequest =>
      mockRequest.method === 'GET' && mockRequest.url === 'api/directories' && mockRequest.params.get('path') === 'publisherManagementWS>qa>/constraints/');
    req.flush([DIRECTORIES_SERVICE_RESPONSE]);
  }));

  it('should get page for path', async(() => {
    pageService.getPage('publisherManagementWS>qa>/constraints/overview')
      .subscribe(page => {
        expect(page).toBeDefined();
        expect(page.path).toEqual('publisherManagementWS>qa>/constraints/overview');
        expect(page.order).toBe(0);
        expect(page.markdown).toBeDefined();
        expect(page.markdown.startsWith('For various reasons')).toBeTruthy();
      });

    const req = httpMock.expectOne(mockRequest =>
      mockRequest.method === 'GET' && mockRequest.url === 'api/pages' && mockRequest.params.get('path') === 'publisherManagementWS>qa>/constraints/overview');
    req.flush([PAGE_SERVICE_RESPONSE]);
  }));

  it('should parse markdown to find external link', async( () => {
    pageService.getPage('publisherManagementWS>qa>/constraints/overview')
      .subscribe(page => {
        expect(page).toBeDefined();
        expect(page.path).toEqual('publisherManagementWS>qa>/constraints/overview');
        expect(page.order).toBe(0);
        expect(page.markdown).toBeFalsy();
        expect(page.externalLink).toBe('http://publisher.corp.kelkoo.net/docs/#/Contact%20Management/getContact');
      });

    const req = httpMock.expectOne(mockRequest =>
      mockRequest.method === 'GET' && mockRequest.url === 'api/pages' && mockRequest.params.get('path') === 'publisherManagementWS>qa>/constraints/overview');
    req.flush([PAGE_WITH_EXTERNAL_LINK_SERVICE_RESPONSE]);
  }));
});
