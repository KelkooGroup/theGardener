import {async, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {MenuService} from './menu.service';
import {MENU_SERVICE_RESPONSE} from '../test/test-data.spec';
import {MenuHierarchy} from '../_models/menu';
import {ActivatedRoute} from '@angular/router';
import {ActivatedRouteStub} from '../test/activated-route-stub.spec';

describe('MenuService', () => {
  let httpMock: HttpTestingController;
  let menuService: MenuService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        MenuService,
        {
          provide: ActivatedRoute,
          useClass: ActivatedRouteStub,
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    httpMock = TestBed.get(HttpTestingController);
    menuService = TestBed.get(MenuService);
    expect(menuService).toBeTruthy();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should return a hierarchy node', () => {
    menuService.hierarchy().subscribe(h => {
      expect(h.name).toBe('Hierarchy root');
      expect(h.projects.length).toBe(0);
      expect(h.children.length).toBe(2);
    });
    const req = httpMock.expectOne('api/menu');
    expect(req.request.method).toBe('GET');
    req.flush(MENU_SERVICE_RESPONSE);

  });

  it('should return the partial hierarchy for the selected node', () => {
    menuService.getMenuForSelectedRootNode('eng').subscribe(h => {
      expect(h.name).toBe('Engineering view');
      expect(h.projects.length).toBe(0);
      expect(h.children.length).toBe(1);
    });
    const req = httpMock.expectOne('api/menu');
    expect(req.request.method).toBe('GET');
    req.flush(MENU_SERVICE_RESPONSE);

    menuService.getMenuForSelectedRootNode('biz').subscribe(h => {
      expect(h.name).toBe('Business view');
      expect(h.projects.length).toBe(1);
      expect(h.children.length).toBe(0);
    });
    const req2 = httpMock.expectOne('api/menu');
    expect(req2.request.method).toBe('GET');
    req2.flush(MENU_SERVICE_RESPONSE);

  });

  it('should build menu hierarchy', async(() => {
    const activatedRoute = TestBed.get(ActivatedRoute) as any;
    activatedRoute.testUrl = [{path: 'app'},
      {path: 'documentation'},
      {path: 'navigate'},
      {path: 'eng'},
    ];
    const expectedMenu: Array<MenuHierarchy> = [
      {
        name: 'library',
        label: 'Library system group',
        type: 'Node',
        depth: 0,
        children: [
          {
            name: 'suggestion',
            label: 'Suggestion system',
            type: 'Node',
            depth: 1,
            children: [
              {
                name: 'suggestionsReports',
                label: 'Suggestions Reports',
                type: 'Project',
                depth: 2,
                stableBranch: 'master',
                route: 'suggestionsReports',
                children: [
                  {
                    name: 'master',
                    label: 'master',
                    type: 'Branch',
                    depth: 3,
                    route: 'suggestionsReports>master',
                    children: [],
                  }
                ]
              },
              {
                name: 'suggestionsWS',
                label: 'Suggestions WebServices',
                type: 'Project',
                depth: 2,
                stableBranch: 'master',
                route: 'suggestionsWS',
                children: [
                  {
                    name: 'master',
                    label: 'master',
                    type: 'Branch',
                    depth: 3,
                    route: 'suggestionsWS>master',
                    children: [
                      {
                        name: 'root',
                        label: 'SuggestionsWS',
                        description: 'Suggestions WebServices',
                        type: 'Directory',
                        depth: 4,
                        order: 0,
                        route: 'suggestionsWS>master>/',
                        children: [
                          {
                            name: 'suggestions',
                            label: 'Suggestions',
                            description: 'Suggestions...',
                            type: 'Directory',
                            depth: 5,
                            order: 0,
                            route: 'suggestionsWS>master>/suggestions/',
                            children: []
                          },
                          {
                            name: 'admin',
                            label: 'Admin',
                            description: 'Administration...',
                            type: 'Directory',
                            depth: 5,
                            order: 1,
                            route: 'suggestionsWS>master>/admin/',
                            children: []
                          },
                        ]
                      }
                    ],
                  },
                  {
                    name: 'bugfix/351',
                    label: 'bugfix/351',
                    type: 'Branch',
                    depth: 3,
                    route: 'suggestionsWS>bugfix/351',
                    children: [],
                  }

                ]
              }
            ]
          },
          {
            name: 'user',
            label: 'User system',
            type: 'Node',
            depth: 1,
            children: [
              {
                name: 'usersWS',
                label: 'Users WebServices',
                type: 'Project',
                depth: 2,
                stableBranch: 'master',
                route: 'usersWS',
                children: [
                  {
                    name: 'master',
                    label: 'master',
                    type: 'Branch',
                    depth: 3,
                    route: 'usersWS>master',
                    children: [],
                  }
                ]
              }
            ]
          },
          {
            name: 'search',
            label: 'Search system',
            type: 'Node',
            depth: 1,
            children: []
          }
        ]
      }
    ];
    menuService.getMenuHierarchyForSelectedNode('eng').subscribe(h => {
      expect(h).toEqual(expectedMenu);
    });
    const req = httpMock.expectOne('api/menu');
    expect(req.request.method).toBe('GET');
    req.flush(MENU_SERVICE_RESPONSE);

  }));
});
