import {async, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';

import {DocumentationService} from './documentation.service';
import {
  DocumentationBranch,
  DocumentationFeature,
  DocumentationNode,
  DocumentationNodeApi,
  DocumentationProject,
  DocumentationScenario, DocumentationStepTextFragment
} from '../_models/documentation';

describe('DocumentationService', () => {
  let httpMock: HttpTestingController;
  let documentationService: DocumentationService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DocumentationService]
    }).compileComponents();
  }));

  beforeEach(() => {
    httpMock = TestBed.get(HttpTestingController);
    documentationService = TestBed.get(DocumentationService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(TestBed.get(DocumentationService)).toBeTruthy();
  });

  it('should create the right object structure with API response', async(() => {
    documentationService.generateDocumentation('project=toto').subscribe(result => {
      expect(result).toBeTruthy();
      expect(result.length).toBe(1);
      const docNode: DocumentationNode = result[0];
      expect(docNode).toEqual(jasmine.any(DocumentationNode));
      expect(docNode.localId).toEqual('suggestion');
      expect(docNode.nodeId).toEqual('_suggestion');
      expect(docNode.level).toEqual(1);
      expect(docNode.projects.length).toEqual(1);
      expect(docNode.children.length).toEqual(0);
      const project = docNode.projects[0];
      expect(project).toBeTruthy();
      expect(project).toEqual(jasmine.any(DocumentationProject));
      expect(project.localId).toEqual('suggestions_webservices');
      expect(project.nodeId).toEqual('_suggestion_suggestions_webservices');
      expect(project.branch).toBeTruthy();
      const branch: DocumentationBranch = project.branch;
      expect(branch.localId).toEqual('master');
      expect(branch.features.length).toBe(1);
      const feature: DocumentationFeature = branch.features[0];
      expect(feature.localId).toBe('test-features-provide_book_suggestions-feature');
      expect(feature.level).toBe(3);
      expect(feature.background).toBeFalsy();
      expect(feature.scenarios.length).toBe(1);
      const scenario: DocumentationScenario = feature.scenarios[0];
      expect(scenario.type).toEqual('scenario');
      expect(scenario.localId).toEqual('providing_several_book_suggestions');
      expect(scenario.steps.length).toEqual(3);
      const step1 = scenario.steps[0];
      expect(step1.hasTable).toBeFalsy();
      expect(step1.text.length).toEqual(1);
      expect(step1.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      const step1TextFragment: DocumentationStepTextFragment = step1.text[0];
      expect(step1TextFragment.text).toEqual('a user');
      const step2 = scenario.steps[1];
      expect(step2.hasTable).toBeFalsy();
      expect(step2.text.length).toEqual(1);
      expect(step2.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      const step2TextFragment: DocumentationStepTextFragment = step2.text[0];
      expect(step2TextFragment.text).toEqual('we ask for suggestions');
      const step3 = scenario.steps[2];
      expect(step3.hasTable).toBeFalsy();
      expect(step3.text.length).toEqual(1);
      expect(step3.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      const step3TextFragment: DocumentationStepTextFragment = step3.text[0];
      expect(step3TextFragment.text).toEqual('the suggestions are popular and available books adapted to the age of the user');
    });
    const req = httpMock.expectOne('api/generateDocumentation?project=toto');
    expect(req.request.method).toBe('GET');
    req.flush(MOCK_RESPONSE);
  }));

  it('should create the right object structure with API response with step arguments', async(() => {
    documentationService.generateDocumentation('project=toto').subscribe(result => {
      expect(result).toBeTruthy();
      expect(result.length).toBe(1);
      const docNode: DocumentationNode = result[0];
      const project = docNode.projects[0];
      const branch: DocumentationBranch = project.branch;
      const feature: DocumentationFeature = branch.features[0];
      const scenario: DocumentationScenario = feature.scenarios[0];
      expect(scenario.type).toEqual('scenario');
      expect(scenario.localId).toEqual('one_service_on_which_the_suggestion_system_depends_on_is_down');
      expect(scenario.steps.length).toEqual(4);

      const step1 = scenario.steps[0];
      expect(step1.hasTable).toBeFalsy();
      expect(step1.text.length).toEqual(3);
      expect(step1.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      const step1TextFragment: DocumentationStepTextFragment = step1.text[0];
      expect(step1TextFragment.text).toEqual('the user ');
      expect(step1TextFragment.isParameter).toBeFalsy();
      const step1TextFragment2: DocumentationStepTextFragment = step1.text[1];
      expect(step1TextFragment2.text).toEqual('Tim');
      expect(step1TextFragment2.isParameter).toBeTruthy();
      const step1TextFragment3: DocumentationStepTextFragment = step1.text[2];
      expect(step1TextFragment3.text).toEqual('');
      expect(step1TextFragment3.isParameter).toBeFalsy();

      const step2 = scenario.steps[1];
      expect(step2.hasTable).toBeFalsy();
      expect(step2.text.length).toEqual(1);
      expect(step2.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      const step2TextFragment: DocumentationStepTextFragment = step2.text[0];
      expect(step2TextFragment.text).toEqual('impossible to get information on the user');

      const step3 = scenario.steps[2];
      expect(step3.hasTable).toBeFalsy();
      expect(step3.text.length).toEqual(5);
      expect(step3.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      const step3TextFragment: DocumentationStepTextFragment = step3.text[0];
      expect(step3TextFragment.text).toEqual('we ask for ');
      expect(step3TextFragment.isParameter).toBeFalsy();
      const step3TextFragment1: DocumentationStepTextFragment = step3.text[1];
      expect(step3TextFragment1.text).toEqual('3');
      expect(step3TextFragment1.isParameter).toBeTruthy();
      const step3TextFragment2: DocumentationStepTextFragment = step3.text[2];
      expect(step3TextFragment2.text).toEqual(' suggestions from ');
      expect(step3TextFragment2.isParameter).toBeFalsy();
      const step3TextFragment3: DocumentationStepTextFragment = step3.text[3];
      expect(step3TextFragment3.text).toEqual('2');
      expect(step3TextFragment3.isParameter).toBeTruthy();
      const step3TextFragment4: DocumentationStepTextFragment = step3.text[4];
      expect(step3TextFragment4.text).toEqual(' different categories');
      expect(step3TextFragment4.isParameter).toBeFalsy();

      const step4 = scenario.steps[3];
      expect(step4.hasTable).toBeFalsy();
      expect(step4.text.length).toEqual(1);
      expect(step4.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      const step4TextFragment: DocumentationStepTextFragment = step4.text[0];
      expect(step4TextFragment.text).toEqual('the system is temporary not available');
    });
    const req = httpMock.expectOne('api/generateDocumentation?project=toto');
    expect(req.request.method).toBe('GET');
    req.flush(RESPONSE_WITH_STEP_ARGUMENTS);
  }));

  it('should create the right object structure with API response with multi lines step', async(() => {
    documentationService.generateDocumentation('project=toto').subscribe(result => {
      expect(result).toBeTruthy();
      expect(result.length).toBe(1);
      const docNode: DocumentationNode = result[0];
      const project = docNode.projects[0];
      const branch: DocumentationBranch = project.branch;
      const feature: DocumentationFeature = branch.features[0];
      const scenario: DocumentationScenario = feature.scenarios[0];
      expect(scenario.type).toEqual('scenario');
      expect(scenario.localId).toEqual('suggestions_of_popular_and_available_books_adapted_to_the_age_of_the_user');
      expect(scenario.steps.length).toEqual(5);

      const step2 = scenario.steps[1];
      expect(step2.hasTable).toBeFalsy();
      expect(step2.text.length).toEqual(3);
      expect(step2.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      expect(step2.text[0].text).toEqual('he is ');
      expect(step2.text[0].isParameter).toBeFalsy();
      expect(step2.text[1].text).toEqual('4');
      expect(step2.text[1].isParameter).toBeTruthy();
      expect(step2.text[2].text).toEqual(' years old');
      expect(step2.text[2].isParameter).toBeFalsy();

      const step3 = scenario.steps[2];
      expect(step3.hasTable).toBeTruthy();
      expect(step3.text.length).toEqual(1);
      expect(step3.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      expect(step3.text[0].text).toEqual('the popular categories for this age are');
      expect(step3.table.headers).toEqual({'0': 'categoryId', '1': 'categoryName'});
      expect(step3.table.rows.length).toBe(3);
      expect(step3.table.rows[0].values).toEqual({0: 'cat1', 1: 'Walt Disney'});
      expect(step3.table.rows[1].values).toEqual({0: 'cat2', 1: 'Picture books'});
      expect(step3.table.rows[2].values).toEqual({0: 'cat3', 1: 'Bedtime stories'});
    });
    const req = httpMock.expectOne('api/generateDocumentation?project=toto');
    expect(req.request.method).toBe('GET');
    req.flush(RESPONSE_WITH_MULTI_LINES_SPECS);
  }));

  it('should create the right object structure with API response with multi lines step', async(() => {
    documentationService.generateDocumentation('project=toto').subscribe(result => {
      expect(result).toBeTruthy();
      expect(result.length).toBe(1);
      const docNode: DocumentationNode = result[0];
      const project = docNode.projects[0];
      const branch: DocumentationBranch = project.branch;
      const feature: DocumentationFeature = branch.features[0];
      const scenario: DocumentationScenario = feature.scenarios[0];
      expect(scenario.type).toEqual('scenario');
      expect(scenario.localId).toEqual('unknown_user,_no_suggestion');
      expect(scenario.steps.length).toEqual(4);

      expect(scenario.examples).toBeTruthy();
      expect(scenario.examples.table).toBeTruthy();
      expect(scenario.examples.table.headers).toEqual({0: 'user_name', 1: 'number_suggestions'});
      expect(scenario.examples.table.rows.length).toBe(2);
      expect(scenario.examples.table.rows[0].values).toEqual({0:'Lise', 1:'2'});
      expect(scenario.examples.table.rows[1].values).toEqual({0:'Tim', 1:'1'});

      const step0 = scenario.steps[0];
      expect(step0.text.length).toBe(3);
      expect(step0.text[0].text).toBe('the user ');
      expect(step0.text[0].isParameter).toBeFalsy();
      expect(step0.text[1].text).toBe('<user_name>');
      expect(step0.text[1].isParameter).toBeTruthy();
      expect(step0.text[2].text).toBe('');
      expect(step0.text[2].isParameter).toBeFalsy();

      const step2 = scenario.steps[2];
      expect(step2.text.length).toEqual(3);
      expect(step2.text[0]).toEqual(jasmine.any(DocumentationStepTextFragment));
      expect(step2.text[0].text).toEqual('we ask for ');
      expect(step2.text[0].isParameter).toBeFalsy();
      expect(step2.text[1].text).toEqual('<number_suggestions>');
      expect(step2.text[1].isParameter).toBeTruthy();
      expect(step2.text[2].text).toEqual(' suggestions');
      expect(step2.text[2].isParameter).toBeFalsy();
    });
    const req = httpMock.expectOne('api/generateDocumentation?project=toto');
    expect(req.request.method).toBe('GET');
    req.flush(RESPONSE_WITH_SCENARIO_OUTLINE);
  }));
});

const MOCK_RESPONSE: DocumentationNodeApi = {
  'id': '.',
  'slugName': 'root',
  'name': 'Hierarchy root',
  'childrenLabel': 'Views',
  'childLabel': 'View',
  'projects': [],
  'children': [{
    'id': '.01.',
    'slugName': 'suggestion',
    'name': 'Suggestion system',
    'childrenLabel': 'Projects',
    'childLabel': 'Project',
    'projects': [{
      'id': 'suggestionsWS',
      'name': 'Suggestions WebServices',
      'branches': [{
        'id': '1',
        'name': 'master',
        'isStable': true,
        'features': [{
          'id': '1',
          'path': 'test/features/provide_book_suggestions.feature',
          'tags': [],
          'keyword': 'Feature',
          'name': 'As a user Tim, I want some book suggestions so that I can do some discovery',
          'description': '',
          'scenarios': [{
            'keyword': 'Scenario',
            'name': 'providing several book suggestions',
            'description': '',
            'tags': ['draft', 'level_0_high_level', 'nominal_case'],
            'abstractionLevel': 'level_0_high_level',
            'id': '1',
            'caseType': 'nominal_case',
            'steps': [{
              'id': '0',
              'keyword': 'Given',
              'text': 'a user',
              'argument': [],
            }, {
              'id': '1',
              'keyword': 'When',
              'text': 'we ask for suggestions',
              'argument': []
            }, {
              'id': '2',
              'keyword': 'Then',
              'text': 'the suggestions are popular and available books adapted to the age of the user',
              'argument': []
            }],
            'workflowStep': 'draft'
          }],
          'comments': [],
        }]
      }]
    }],
    'children': []
  }]
};

const RESPONSE_WITH_STEP_ARGUMENTS: DocumentationNodeApi = {
  id: '.',
  slugName: 'root',
  name: 'Hierarchy root',
  childrenLabel: 'Views',
  childLabel: 'View',
  projects: [],
  children: [{
    id: '.01.',
    slugName: 'suggestion',
    name: 'Suggestion system',
    childrenLabel: 'Projects',
    childLabel: 'Project',
    projects: [{
      id: 'suggestionsWS',
      name: 'Suggestions WebServices',
      branches: [{
        id: '1',
        name: 'master',
        isStable: true,
        features: [{
          id: '1',
          path: 'test/features/provide_book_suggestions.feature',
          tags: [],
          keyword: 'Feature',
          name: 'As a user, I want some book suggestions so that I can do some discovery',
          description: '',
          scenarios: [{
            keyword: 'Scenario',
            name: 'one service on which the suggestion system depends on is down',
            description: '',
            tags: ['error_case', 'level_1_specification', 'valid'],
            abstractionLevel: 'level_1_specification',
            id: '1',
            caseType: 'error_case',
            steps: [{
              id: '0',
              keyword: 'Given',
              text: 'the user "Tim"',
              argument: []
            }, {
              id: '1',
              keyword: 'And',
              text: 'impossible to get information on the user',
              argument: []
            }, {
              id: '2',
              keyword: 'When',
              text: 'we ask for "3" suggestions from "2" different categories',
              argument: []
            }, {
              id: '3',
              keyword: 'Then',
              text: 'the system is temporary not available',
              argument: []
            }],
            workflowStep: 'valid'
          }],
          comments: []
        }]
      }]
    }],
    children: []
  }]
}

const RESPONSE_WITH_MULTI_LINES_SPECS: DocumentationNodeApi = {
  "id": ".",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "projects": [],
  "children": [{
    "id": ".01.",
    "slugName": "suggestion",
    "name": "Suggestion system",
    "childrenLabel": "Projects",
    "childLabel": "Project",
    "projects": [{
      "id": "suggestionsWS",
      "name": "Suggestions WebServices",
      "branches": [{
        "id": "1",
        "name": "master",
        "isStable": true,
        "features": [{
          "id": "1",
          "path": "test/features/provide_book_suggestions.feature",
          "tags": [],
          "keyword": "Feature",
          "name": "As a user, I want some book suggestions so that I can do some discovery",
          "description": "",
          "scenarios": [{
            "keyword": "Scenario",
            "name": "suggestions of popular and available books adapted to the age of the user",
            "description": "",
            "tags": [],
            "abstractionLevel": "level_1_specification",
            "id": "1",
            "caseType": "nominal_case",
            "steps": [{
              "id": "0",
              "keyword": "Given",
              "text": "the user \"Tim\"",
              "argument": []
            }, {
              "id": "1",
              "keyword": "And",
              "text": "he is \"4\" years old",
              "argument": []
            }, {
              "id": "2",
              "keyword": "And",
              "text": "the popular categories for this age are",
              "argument": [
                ["categoryId", "categoryName"],
                ["cat1", "Walt Disney"],
                ["cat2", "Picture books"],
                ["cat3", "Bedtime stories"]
              ]
            }, {
              "id": "3",
              "keyword": "When",
              "text": "we ask for \"3\" suggestions from \"2\" different categories",
              "argument": []
            }, {
              "id": "4",
              "keyword": "Then",
              "text": "the suggestions are popular and available books adapted to the age of the user",
              "argument": []
            }],
            "workflowStep": "valid"
          }],
          "comments": []
        }]
      }]
    }],
    "children": []
  }]
}

const RESPONSE_WITH_SCENARIO_OUTLINE: DocumentationNodeApi = {
  "id": ".",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "projects": [],
  "children": [{
    "id": ".01.",
    "slugName": "suggestion",
    "name": "Suggestion system",
    "childrenLabel": "Projects",
    "childLabel": "Project",
    "projects": [{
      "id": "suggestionsWS",
      "name": "Suggestions WebServices",
      "branches": [{
        "id": "1",
        "name": "master",
        "isStable": true,
        "features": [{
          "id": "1",
          "path": "test/features/provide_book_suggestions.feature",
          "tags": [],
          "keyword": "Feature",
          "name": "As a user, I want some book suggestions so that I can do some discovery",
          "description": "",
          "scenarios": [{
            "keyword": "Scenario Outline",
            "name": "unknown user, no suggestion",
            "examples": [{
              "id": "0",
              "keyword": "Examples",
              "description": "",
              "tableHeader": ["user_name", "number_suggestions"],
              "tableBody": [
                ["Lise", "2"],
                ["Tim", "1"]
              ]
            }],
            "description": "",
            "tags": ["error_case", "level_1_specification", "valid"],
            "abstractionLevel": "level_1_specification",
            "id": "1",
            "caseType": "error_case",
            "steps": [{
              "id": "0",
              "keyword": "Given",
              "text": "the user \"<user_name>\"",
              "argument": []
            }, {
              "id": "1",
              "keyword": "And",
              "text": "he is unknown",
              "argument": []
            }, {
              "id": "2",
              "keyword": "When",
              "text": "we ask for \"<number_suggestions>\" suggestions",
              "argument": []
            }, {
              "id": "3",
              "keyword": "Then",
              "text": "there is no suggestions",
              "argument": []
            }],
            "workflowStep": "valid"
          }],
          "comments": []
        }]
      }]
    }],
    "children": []
  }]
};
