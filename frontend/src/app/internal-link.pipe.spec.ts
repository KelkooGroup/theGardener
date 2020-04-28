import {InternalLinkPipe} from './internal-link.pipe';
import {ActivatedRouteStub} from './_testUtils/activated-route-stub.spec';
import {RouteService} from "./_services/route.service";

describe('InternalLinkPipe', () => {
  let pipe: InternalLinkPipe;
  let activatedRoute;


  beforeEach(() => {
    activatedRoute = new ActivatedRouteStub() as any;
    let routeService =  new RouteService();
    activatedRoute.testParams = {nodes: 'Tools',project: 'theGardener',branch: 'master',directories: '_Guide_Write',page: 'Basics'};
    pipe = new InternalLinkPipe(activatedRoute,routeService);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('replace occurences of internal legacy links', () => {
    expect(pipe.transform(LEGACY_SIMPLE_HTML_INPUT)).toBe(LEGACY_SIMPLE_HTML_OUTPUT);
    expect(pipe.transform(LEGACY_SIMPLE_HTML_INPUT_WITH_HIERARCHY)).toBe(LEGACY_SIMPLE_HTML_OUTPUT_WITH_HIERARCHY);
    expect(pipe.transform(LEGACY_COMPLEX_HTML_INPUT)).toBe(LEGACY_COMPLEX_HTML_OUTPUT);
    expect(pipe.transform(LEGACY_HTML_INPUT_WITH_OTHER_LINK)).toBe(LEGACY_HTML_OUTPUT_WITH_OTHER_LINK);
  });

  it('replace occurences of internal links', () => {
    expect(pipe.transform(SIMPLE_HTML_INPUT)).toBe(SIMPLE_HTML_OUTPUT);
    expect(pipe.transform(SIMPLE_HTML_INPUT_PARTIAL_PATH)).toBe(SIMPLE_HTML_OUTPUT_PARTIAL_PATH);
  });

  it('replace occurences of internal relative links', () => {
    expect(pipe.transform(SIMPLE_HTML_INPUT_RELATIVE_SAME_DIRECTORY)).toBe(SIMPLE_HTML_OUTPUT_RELATIVE_SAME_DIRECTORY);
    expect(pipe.transform(SIMPLE_HTML_INPUT_RELATIVE_PARENT_DIRECTORY)).toBe(SIMPLE_HTML_OUTPUT_RELATIVE_PARENT_DIRECTORY);
  });

});


const SIMPLE_HTML_INPUT_RELATIVE_SAME_DIRECTORY = '<p>Note 2: <code><a href="./OpenApi.md">link to OpenAPI</a>.</p>';

const SIMPLE_HTML_OUTPUT_RELATIVE_SAME_DIRECTORY = '<p>Note 2: <code><a onclick=\"navigateTo(\'app/documentation/navigate/Tools/theGardener/master/_Guide_Write/OpenApi\')\">link to OpenAPI</a>.</p>';

const SIMPLE_HTML_INPUT_RELATIVE_PARENT_DIRECTORY = '<p>Note 2: <code><a href="../../Changelog.md">link to OpenAPI</a>.</p>';

const SIMPLE_HTML_OUTPUT_RELATIVE_PARENT_DIRECTORY = '<p>Note 2: <code><a onclick=\"navigateTo(\'app/documentation/navigate/Tools/theGardener/master/_/Changelog\')\">link to OpenAPI</a>.</p>';

const SIMPLE_HTML_INPUT = '<p>Note 2: <code>Navigate with full path</code> parameter must be encoded using <a href="thegardener://navigate/Documentation/theGardener/master/_Write/Basics">URL encoding</a>.</p>';

const SIMPLE_HTML_OUTPUT = '<p>Note 2: <code>Navigate with full path</code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/Documentation/theGardener/master/_Write/Basics\')\">URL encoding</a>.</p>';

const SIMPLE_HTML_INPUT_PARTIAL_PATH = '<p>Note 2: <code>Navigate with a part of the path</code> parameter must be encoded using <a href="thegardener://navigate/Documentation/theGardener">URL encoding</a>.</p>';

const SIMPLE_HTML_OUTPUT_PARTIAL_PATH = '<p>Note 2: <code>Navigate with a part of the path</code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/Documentation/theGardener\')\">URL encoding</a>.</p>';

const LEGACY_SIMPLE_HTML_INPUT = '<p>Note 2: <code>merchantUrl</code> parameter must be encoded using <a href="thegardener://path=theGardener>master>_features_/administration">URL encoding</a>.</p>';

const LEGACY_SIMPLE_HTML_OUTPUT = '<p>Note 2: <code>merchantUrl</code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/Tools/theGardener/master/_features/administration\')\">URL encoding</a>.</p>';

const LEGACY_SIMPLE_HTML_INPUT_WITH_HIERARCHY = '<p>Note 2: <code>merchantUrl</code> parameter must be encoded using <a href="thegardener://navigate/_publisher;path=publisherSystems>master>_Public_/publisherServices">URL encoding</a>.</p>';

const LEGACY_SIMPLE_HTML_OUTPUT_WITH_HIERARCHY = '<p>Note 2: <code>merchantUrl</code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/_publisher/publisherSystems/master/_Public/publisherServices\')\">URL encoding</a>.</p>';

const LEGACY_COMPLEX_HTML_INPUT = '<p>Note 2: <code>merchantUrl <a href="thegardener://path=theGardener>master>_features_/administration">internal link1</a> </code> parameter must be encoded using <a href="thegardener://navigate/_publisher;path=publisherSystems>master>_Public_/publisherServices">internal link2</a>.</p>';

const LEGACY_COMPLEX_HTML_OUTPUT = '<p>Note 2: <code>merchantUrl <a onclick=\"navigateTo(\'app/documentation/navigate/Tools/theGardener/master/_features/administration\')\">internal link1</a> </code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/_publisher/publisherSystems/master/_Public/publisherServices\')\">internal link2</a>.</p>';

const LEGACY_HTML_INPUT_WITH_OTHER_LINK = '<p>Note 2: <code>merchantUrl <a href="/app/documentation/navigate/_publisher;path=shoppingAPI%3Eqa%3E_Tools_/requestBuilder">link</a></code> parameter must be encoded using <a href="thegardener://path=theGardener>master>_features_/administration">URL encoding</a>.</p>';

const LEGACY_HTML_OUTPUT_WITH_OTHER_LINK = '<p>Note 2: <code>merchantUrl <a href=\"/app/documentation/navigate/_publisher;path=shoppingAPI%3Eqa%3E_Tools_/requestBuilder\">link</a></code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/Tools/theGardener/master/_features/administration\')\">URL encoding</a>.</p>';
