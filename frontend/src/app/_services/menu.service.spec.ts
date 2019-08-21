import {async, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {MenuService} from './menu.service';
import {
  MENU_HEADER_SERVICE_RESPONSE,
  MENU_SERVICE_RESPONSE,
  MENU_SUBMENU_SERVICE_RESPONSE
} from '../test/test-data.spec';
import {MenuHierarchy} from '../_models/menu';

describe('MenuService', () => {
  let httpMock: HttpTestingController;
  let menuService: MenuService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        MenuService,
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

  it('should return a hierarchy node', async(() => {
    menuService.hierarchy().subscribe(h => {
      expect(h.name).toBe('Hierarchy root');
      expect(h.projects.length).toBe(0);
      expect(h.children.length).toBe(2);
    });
    const req = httpMock.expectOne('api/menu');
    expect(req.request.method).toBe('GET');
    req.flush(MENU_SERVICE_RESPONSE);

  }));

  it('should provide menu header', async(() => {
    menuService.getMenuHeader().subscribe(menu => {
      expect(menu.name).toBe('Hierarchy root');
      expect(menu.children.length).toBe(2);
      expect(menu.children[0].name).toBe('Engineering view');
      expect(menu.children[0].id).toBe('.01.');
      expect(menu.children[1].name).toBe('Business view');
      expect(menu.children[1].id).toBe('.02.');
    });
    const req = httpMock.expectOne('api/menu/header');
    expect(req.request.method).toBe('GET');
    req.flush(MENU_HEADER_SERVICE_RESPONSE);
  }));

  it('should provide submenu for selected node', async(() => {
    menuService.getSubMenuForNode('.01.').subscribe(submenu => {
      expect(submenu).toEqual(EXPECTED_MENU_FOR_ENGINEERING_VIEW);
    });
    const req = httpMock.expectOne('api/menu/submenu/.01.');
    expect(req.request.method).toBe('GET');
    req.flush(MENU_SUBMENU_SERVICE_RESPONSE);
  }));

  it('should return the partial hierarchy for the selected node', async(() => {
    menuService.getMenuHierarchyForSelectedNode('_eng').subscribe(h => {
      expect(h.length).toBe(1);
      expect(h[0].label).toBe('Library system group');
      expect(h[0].children.length).toBe(3);
    });
    const req = httpMock.expectOne('api/menu');
    expect(req.request.method).toBe('GET');
    req.flush(MENU_SERVICE_RESPONSE);

    menuService.getMenuHierarchyForSelectedNode('_biz').subscribe(h => {
      expect(h.length).toBe(1);
      expect(h[0].name).toBe('suggestionsWS');
      expect(h[0].type).toBe('Project');
      expect(h[0].children.length).toBe(2);
    });
    const req2 = httpMock.expectOne('api/menu');
    expect(req2.request.method).toBe('GET');
    req2.flush(MENU_SERVICE_RESPONSE);

  }));

  it('should build menu hierarchy', async(() => {
    menuService.getMenuHierarchyForSelectedNode('_eng').subscribe(h => {
      expect(h).toEqual(EXPECTED_MENU_FOR_ENGINEERING_VIEW);
    });
    const req = httpMock.expectOne('api/menu');
    expect(req.request.method).toBe('GET');
    req.flush(MENU_SERVICE_RESPONSE);

  }));
});

const EXPECTED_MENU_FOR_ENGINEERING_VIEW: Array<MenuHierarchy> = [
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
