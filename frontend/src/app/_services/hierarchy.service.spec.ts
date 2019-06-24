import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {async, TestBed} from '@angular/core/testing';
import {HierarchyService} from './hierarchy.service';
import {HierarchyNodeApi} from '../_models/hierarchy';

describe('HierarchyService', () => {
  let httpMock: HttpTestingController;
  let hierarchyService: HierarchyService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [HierarchyService]
    }).compileComponents();
  }));

  beforeEach(() => {
    httpMock = TestBed.get(HttpTestingController);
    hierarchyService = TestBed.get(HierarchyService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(TestBed.get(HierarchyService)).toBeTruthy();
  });

  it('should parse service response', async(() => {
    hierarchyService.hierarchy().subscribe(h => {
      expect(h.id).toBe('.');
      expect(h.name).toBe('Hierarchy root');
      expect(h.childLabel).toBe('View');
      expect(h.childrenLabel).toBe('Views');
      expect(h.children.length).toBe(2);
      expect(h.projects.length).toBe(0);

      const engView = h.children[0];
      expect(engView.id).toEqual('.01.');
      expect(engView.name).toEqual('Engineering view');
      expect(engView.childLabel).toEqual('System group');
      expect(engView.childrenLabel).toEqual('System groups');
      expect(engView.projects.length).toBe(0);
      expect(engView.children.length).toBe(1);

      const libGroup = engView.children[0];
      expect(libGroup.id).toEqual('.01.01.');
      expect(libGroup.name).toEqual('Library system group');
      expect(libGroup.childLabel).toEqual('System');
      expect(libGroup.childrenLabel).toEqual('Systems');
      expect(libGroup.projects.length).toBe(0);
      expect(libGroup.children.length).toBe(3);

      const suggestion = libGroup.children[0];
      expect(suggestion.id).toEqual('.01.01.01.');
      expect(suggestion.name).toEqual('Suggestion system');
      expect(suggestion.projects.length).toBe(2);

      const suggestionReports = suggestion.projects[0];
      expect(suggestionReports.id).toEqual('suggestionsReports');
      expect(suggestionReports.branches.length).toBe(1);

      const suggestionWsProject = suggestion.projects[1];
      expect(suggestionWsProject.id).toEqual('suggestionsWS');
      expect(suggestionWsProject.displayName).toBe('Suggestions WebServices');
      expect(suggestionWsProject.stableBranch.name).toBe('master');
      expect(suggestionWsProject.branches.length).toBe(2);
      expect(suggestionWsProject.branches[0].name).toBe('master');
      // TODO refactor : what if response is parsed in a different order? Maybe we should let the UI handle path with '/'
      expect(suggestionWsProject.branches[0].features.length).toBe(5);
      expect(suggestionWsProject.branches[0].features[0].displayName).toBe('admin :');
      expect(suggestionWsProject.branches[0].features[1].displayName).toBe('admin_book_suggestions.feature');
      expect(suggestionWsProject.branches[0].features[2].displayName).toBe('provide :');
      expect(suggestionWsProject.branches[0].features[3].displayName).toBe('provide_book_suggestions.feature');
      expect(suggestionWsProject.branches[0].features[4].displayName).toBe('provide_other_suggestions.feature');

      expect(suggestionWsProject.branches[1].name).toBe('bugfix/351');
      expect(suggestionWsProject.branches[1].features.length).toBe(0);

      const user = libGroup.children[1];
      expect(user.id).toEqual('.01.01.02.');
      expect(user.name).toEqual('User system');
      expect(user.projects.length).toBe(1);
      expect(user.projects[0].id).toEqual('usersWS');
      expect(user.projects[0].displayName).toEqual('Users WebServices');
      expect(user.projects[0].branches.length).toBe(1);

      const search = libGroup.children[2];
      expect(search.id).toEqual('.01.01.03.');
      expect(search.name).toEqual('Search system');
      expect(search.slugName).toEqual('search');
      expect(search.children.length).toBe(0);
      expect(search.projects.length).toBe(0);

      const bizView = h.children[1];
      expect(bizView.id).toEqual('.02.');
      expect(bizView.name).toEqual('Business view');
      expect(bizView.childLabel).toEqual('Unit');
      expect(bizView.childrenLabel).toEqual('Units');
      expect(bizView.projects.length).toBe(1);
      expect(bizView.children.length).toBe(0);

      const suggestionsWS = bizView.projects[0];
      expect(suggestionsWS.id).toBe('suggestionsWS');
      expect(suggestionsWS.displayName).toBe('Suggestions WebServices');
      expect(suggestionsWS.stableBranch.name).toBe('master');
      expect(suggestionsWS.branches.length).toBe(2);
      expect(suggestionsWS.branches[0].name).toBe('master');
      // TODO refactor : what if response is parsed in a different order? Maybe we should let the UI handle path with '/'
      expect(suggestionsWS.branches[0].features.length).toBe(5);
      expect(suggestionsWS.branches[0].features[0].displayName).toBe('admin :');
      expect(suggestionsWS.branches[0].features[1].displayName).toBe('admin_book_suggestions.feature');
      expect(suggestionsWS.branches[0].features[2].displayName).toBe('provide :');
      expect(suggestionsWS.branches[0].features[3].displayName).toBe('provide_book_suggestions.feature');
      expect(suggestionsWS.branches[0].features[4].displayName).toBe('provide_other_suggestions.feature');

      expect(suggestionsWS.branches[1].name).toBe('bugfix/351');
      expect(suggestionsWS.branches[1].features.length).toBe(0);
    });
    const req = httpMock.expectOne('api/menu');
    expect(req.request.method).toBe('GET');
    req.flush(SERVER_RESPONSE);
  }));
});

const SERVER_RESPONSE : Array<HierarchyNodeApi> = [
  {
    "id":".",
    "slugName":"root",
    "name":"Hierarchy root",
    "childrenLabel":"Views",
    "childLabel":"View",
    "projects":[]
  },
  {
    "id":".01.",
    "slugName":"eng",
    "name":"Engineering view",
    "childrenLabel":"System groups",
    "childLabel":"System group",
    "projects":[]
  },
  {
    "id":".01.01.",
    "slugName":"library",
    "name":"Library system group",
    "childrenLabel":"Systems",
    "childLabel":"System",
    "projects":[]
  },
  {
    "id":".01.01.01.",
    "slugName":"suggestion",
    "name":"Suggestion system",
    "childrenLabel":"Projects",
    "childLabel":"Project",
    "projects":[
      {
        "id":"suggestionsReports",
        "label":"Suggestions Reports",
        "stableBranch":"master",
        "branches":[
          {
            "name":"master",
            "features":[]
          }
          ]
      },
      {
        "id":"suggestionsWS",
        "label":"Suggestions WebServices",
        "stableBranch":"master",
        "branches":[
          {
            "name":"master",
            "features": ["admin/admin_book_suggestions.feature", "provide/provide_book_suggestions.feature", "provide/provide_other_suggestions.feature"]
          },
          {
            "name":"bugfix/351",
            "features":[]
          }
          ]
      }
      ]
  },
  {
    "id":".01.01.02.",
    "slugName":"user",
    "name":"User system",
    "childrenLabel":"Projects",
    "childLabel":"Project",
    "projects":[
      {
        "id":"usersWS",
        "label":"Users WebServices",
        "stableBranch":"master",
        "branches":[
          {
            "name":"master",
            "features":[]
          }
          ]
      }
      ]
  },
  {
    "id":".01.01.03.",
    "slugName":"search",
    "name":"Search system",
    "childrenLabel":"Projects",
    "childLabel":"Project",
    "projects":[]
  },
  {
    "id":".02.",
    "slugName":"biz",
    "name":"Business view",
    "childrenLabel":"Units",
    "childLabel":"Unit",
    "projects":[
      {
        "id":"suggestionsWS",
        "label":"Suggestions WebServices",
        "stableBranch":"master",
        "branches":[
          {
            "name":"master",
            "features": ["admin/admin_book_suggestions.feature", "provide/provide_book_suggestions.feature", "provide/provide_other_suggestions.feature"]
          },
          {
            "name":"bugfix/351",
            "features":[]
          }
          ]
      }
      ]
  }
  ]
