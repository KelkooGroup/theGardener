import {async, TestBed} from '@angular/core/testing';

import {PageService} from './page.service';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {DIRECTORIES_SERVICE_RESPONSE, PAGE_SERVICE_RESPONSE} from '../test/test-data.spec';

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
        expect(directories[0].pages[2].order).toBe(3);
        expect(directories[0].pages[3].name).toEqual('for_an_offer');
        expect(directories[0].pages[3].order).toBe(2);
      });


    const req = httpMock.expectOne(mockRequest =>
      mockRequest.method === 'GET' && mockRequest.url === 'api/directories' && mockRequest.params.get('path') === 'publisherManagementWS>qa>/constraints/');
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
        expect(pages[0].markdown.startsWith('For various reasons')).toBeTruthy();
      });

    const req = httpMock.expectOne(mockRequest =>
      mockRequest.method === 'GET' && mockRequest.url === 'api/pages' && mockRequest.params.get('path') === 'publisherManagementWS>qa>/constraints/overview');
    req.flush(PAGE_SERVICE_RESPONSE);
  }));
});
