import { TestBed, waitForAsync } from '@angular/core/testing';

import {PageService} from './page.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {
  DIRECTORIES_SERVICE_RESPONSE,
  PAGE_SERVICE_RESPONSE,
  PAGE_WITH_EXTERNAL_LINK_SERVICE_RESPONSE,
  PAGE_WITH_SCENARIO,
  SEARCH_RESULTS
} from '../_testUtils/test-data.spec';
import {IncludeExternalPagePart, MarkdownPart} from '../_models/page';

describe('PageService', () => {
  let httpMock: HttpTestingController;
  let pageService: PageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        PageService,
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    httpMock = TestBed.inject(HttpTestingController);
    pageService = TestBed.inject(PageService);
    expect(pageService).toBeTruthy();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should get list of pages for path', waitForAsync(() => {
    pageService.getRootDirectoryForPath('publisherManagementWS>qa>_constraints_')
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

  xit('should search pages for keyword', waitForAsync(() => {
        pageService.searchPages('suggestions')
            .subscribe(result => {
                expect(result.items).toBeDefined();
                expect(result.items.length).toBe(1);
                expect(result.items[0].page.label).toBe('The context');
            });


        const req = httpMock.expectOne(mockRequest =>
            mockRequest.method === 'GET' && mockRequest.url === 'api/pages/search' && mockRequest.params.get('keyword') === 'suggestions');
        req.flush([SEARCH_RESULTS]);
    }));


  it('should get page for path', waitForAsync(() => {
    pageService.getPage('publisherManagementWS>qa>_constraints_overview')
      .subscribe(page => {
        expect(page).toBeDefined();
        expect(page.path).toEqual('publisherManagementWS>qa>/constraints/overview');
        expect(page.order).toBe(0);
        expect(page.parts).toBeDefined();
        expect(page.parts.length).toBe(1);
        expect(page.parts[0].type).toBe('markdown');
        expect((page.parts[0].data as MarkdownPart).markdown).toBeDefined();
        expect((page.parts[0].data as MarkdownPart).markdown.startsWith('For various reasons')).toBeTruthy();
      });

    const req = httpMock.expectOne(mockRequest =>
      mockRequest.method === 'GET' && mockRequest.url === 'api/pages' && mockRequest.params.get('path') === 'publisherManagementWS>qa>/constraints/overview');
    req.flush([PAGE_SERVICE_RESPONSE]);
  }));

  it('should parse markdown to find external link', waitForAsync( () => {
    pageService.getPage('publisherManagementWS>qa>_constraints_overview')
      .subscribe(page => {
        expect(page).toBeDefined();
        expect(page.path).toEqual('publisherManagementWS>qa>/constraints/overview');
        expect(page.order).toBe(0);
        expect(page.parts.length).toBe(1);
        expect(page.parts[0].type).toBe('includeExternalPage');
        expect((page.parts[0].data as IncludeExternalPagePart).includeExternalPage).toBe('http://publisher.corp.kelkoo.net/docs/#/Contact%20Management/getContact');
      });

    const req = httpMock.expectOne(mockRequest =>
      mockRequest.method === 'GET' && mockRequest.url === 'api/pages' && mockRequest.params.get('path') === 'publisherManagementWS>qa>/constraints/overview');
    req.flush([PAGE_WITH_EXTERNAL_LINK_SERVICE_RESPONSE]);
  }));

  it('should parse page containing scenarios', waitForAsync( () => {
    pageService.getPage('path')
      .subscribe(page => {
        expect(page).toBeDefined();
        expect(page.path).toEqual('suggestionsWS>master>/context');
        expect(page.order).toBe(0);
        expect(page.parts.length).toBe(3);
        expect(page.parts[0].type).toBe('markdown');
        expect(page.parts[1].type).toBe('scenarios');
        expect((page.parts[0].data as MarkdownPart).markdown).toBeDefined();
      });

    const req = httpMock.expectOne(mockRequest =>
      mockRequest.method === 'GET' && mockRequest.url === 'api/pages' && mockRequest.params.get('path') === 'path');
    req.flush([PAGE_WITH_SCENARIO]);
  }));
});
