import { TestBed, waitForAsync } from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {MenuService} from './menu.service';
import {
    MENU_HEADER_SERVICE_RESPONSE,
    MENU_SERVICE_RESPONSE,
    MENU_SUBMENU_SERVICE_RESPONSE
} from '../_testUtils/test-data.spec';
import {MenuHierarchy, MenuType} from '../_models/menu';

describe('MenuService', () => {
    let httpMock: HttpTestingController;
    let menuService: MenuService;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                MenuService,
            ]
        }).compileComponents();
    }));

    beforeEach(() => {
        httpMock = TestBed.inject(HttpTestingController);
        menuService = TestBed.inject(MenuService);
        expect(menuService).toBeTruthy();
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should return a hierarchy node', waitForAsync(() => {
        menuService.hierarchy().subscribe(h => {
            expect(h.name).toBe('Hierarchy root');
            expect(h.projects.length).toBe(0);
            expect(h.children.length).toBe(2);
        });
        const req = httpMock.expectOne('api/menu');
        expect(req.request.method).toBe('GET');
        req.flush(MENU_SERVICE_RESPONSE);

    }));

    it('should provide menu header', waitForAsync(() => {
        menuService.getMenuHeader().subscribe(menuItems => {
            expect(menuItems.length).toBe(2);
            expect(menuItems[0].label).toBe('publisher');
            expect(menuItems[1].label).toBe('tools');
        });
        const req = httpMock.expectOne('api/menu/header');
        expect(req.request.method).toBe('GET');
        req.flush(MENU_HEADER_SERVICE_RESPONSE);
    }));

    it('should provide submenu for selected node', waitForAsync(() => {
      menuService.getSubMenuForNode('_eng').subscribe(submenu => {
        expect(submenu).toEqual(EXPECTED_MENU_FOR_ENGINEERING_VIEW);
      });
      const req = httpMock.expectOne('api/menu/submenu/_eng');
      expect(req.request.method).toBe('GET');
      req.flush(MENU_SUBMENU_SERVICE_RESPONSE);
    }));

    it('should return the partial hierarchy for the selected node', waitForAsync(() => {
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


});

const EXPECTED_MENU_FOR_ENGINEERING_VIEW: Array<MenuHierarchy> = [
    {
        name: 'library',
        label: 'Library system group',
        type: 'Node' as MenuType,
        depth: 0,
        route: {
            nodes: ['eng', 'library'],
            directories: [] as Array<string>
        },
        children: [
            {
                name: 'suggestion',
                label: 'Suggestion system',
                type: 'Node' as MenuType,
                depth: 1,
                route: {
                    nodes: ['eng', 'library', 'suggestion'],
                    directories: [] as Array<string>
                },
                children: [
                    {
                        name: 'suggestionsReports',
                        label: 'Suggestions Reports',
                        type: 'Project' as MenuType,
                        depth: 2,
                        route: {
                            nodes: ['eng', 'library', 'suggestion'],
                            project: 'suggestionsReports',
                            directories: [] as Array<string>
                        },
                        stableBranch: 'master',
                        children: [
                            {
                                name: 'master',
                                label: 'master',
                                type: 'Branch'as MenuType,
                                depth: 2,
                                route: {
                                    nodes: ['eng', 'library', 'suggestion'],
                                    project: 'suggestionsReports',
                                    branch: '_',
                                    directories: [] as Array<string>
                                },
                                children: [] as Array<MenuHierarchy>,
                            }
                        ]
                    },
                    {
                        name: 'suggestionsWS',
                        label: 'Suggestions WebServices',
                        type: 'Project' as MenuType,
                        depth: 2,
                        route: {
                            nodes: ['eng', 'library', 'suggestion'],
                            project: 'suggestionsWS',
                            directories: [] as Array<string>
                        },
                        stableBranch: 'master',
                        children: [
                            {
                                name: 'master',
                                label: 'master',
                                type: 'Branch' as MenuType,
                                depth: 2,
                                route: {
                                    nodes: ['eng', 'library', 'suggestion'],
                                    project: 'suggestionsWS',
                                    branch: 'master',
                                    directories: [] as Array<string>
                                },
                                children: [
                                    {
                                        name: 'suggestions',
                                        label: 'Suggestions',
                                        description: 'Suggestions...',
                                        type: 'Directory',
                                        depth: 3,
                                        order: 0,
                                        route: {
                                            nodes: ['eng', 'library', 'suggestion'],
                                            project: 'suggestionsWS',
                                            branch: 'master',
                                            directories: ['suggestions']
                                        },
                                        children: [] as Array<MenuHierarchy>
                                    },
                                    {
                                        name: 'admin',
                                        label: 'Admin',
                                        description: 'Administration...',
                                        type: 'Directory'as MenuType,
                                        depth: 3,
                                        order: 1,
                                        route: {
                                            nodes: ['eng', 'library', 'suggestion'],
                                            project: 'suggestionsWS',
                                            branch: 'master',
                                            directories: ['admin']
                                        },
                                        children: [] as Array<MenuHierarchy>
                                    },
                                ]
                            },
                            {
                                name: 'bugfix/351',
                                label: 'bugfix/351',
                                type: 'Branch'as MenuType,
                                depth: 2,
                                route: {
                                    nodes: ['eng', 'library', 'suggestion'],
                                    project: 'suggestionsWS',
                                    branch: 'bugfix/351',
                                    directories: [] as Array<string>
                                },
                                children: [] as Array<MenuHierarchy>,
                            }

                        ]
                    }
                ]
            },
            {
                name: 'user',
                label: 'User system',
                type: 'Node' as MenuType,
                depth: 1,
                route: {
                    nodes: ['eng','library', 'user'],
                    directories: [] as Array<string>
                },
                children: [
                    {
                        name: 'usersWS',
                        label: 'Users WebServices',
                        type: 'Project' as MenuType,
                        depth: 2,
                        route:  {
                            nodes: ['eng','library', 'user'],
                            project: 'usersWS',
                            directories: [] as Array<string>
                        },
                        stableBranch: 'master',
                        children: [
                            {
                                name: 'master',
                                label: 'master',
                                type: 'Branch'as MenuType,
                                depth: 2,
                                route: {
                                    nodes: ['eng','library', 'user'],
                                    project: 'usersWS',
                                    branch: '_',
                                    directories: [] as Array<string>
                                },
                                children: [],
                            }
                        ]
                    }
                ]
            },
            {
                name: 'search',
                label: 'Search system',
                type: 'Node' as MenuType,
                depth: 1,
                route: {
                    nodes: ['eng', 'library' , 'search'],
                    directories: [] as Array<string>
                },
                children: []
            }
        ]
    }
];
