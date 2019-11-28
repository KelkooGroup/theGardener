import {InternalLinkPipe} from './internal-link.pipe';
import {ActivatedRouteStub} from './_testUtils/activated-route-stub.spec';

describe('InternalLinkPipe', () => {
  let pipe: InternalLinkPipe;
  let activatedRoute;


  beforeEach(() => {
    activatedRoute = new ActivatedRouteStub() as any;
    activatedRoute.testParentParams = {name: 'HierarchyNode'};
    pipe = new InternalLinkPipe(activatedRoute);
  });


  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('replace occurences of internal links', () => {
    expect(pipe.transform(SIMPLE_HTML_INPUT)).toBe(SIMPLE_HTML_OUTPUT);
    expect(pipe.transform(SIMPLE_HTML_INPUT_WITH_HIERARCHY)).toBe(SIMPLE_HTML_OUTPUT_WITH_HIERARCHY);
    expect(pipe.transform(COMPLEX_HTML_INPUT)).toBe(COMPLEX_HTML_OUTPUT);
    expect(pipe.transform(HTML_INPUT_WITH_OTHER_LINK)).toBe(HTML_OUTPUT_WITH_OTHER_LINK);
  });
});


const SIMPLE_HTML_INPUT = '<p>Note 2: <code>merchantUrl</code> parameter must be encoded using <a href="thegardener://path=theGardener>master>_features_/administration">URL encoding</a>.</p>';

const SIMPLE_HTML_OUTPUT = '<p>Note 2: <code>merchantUrl</code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/HierarchyNode;path=theGardener>master>_features_/administration\')\">URL encoding</a>.</p>';

const SIMPLE_HTML_INPUT_WITH_HIERARCHY = '<p>Note 2: <code>merchantUrl</code> parameter must be encoded using <a href="thegardener://navigate/_publisher;path=publisherSystems>master>_Public_/publisherServices">URL encoding</a>.</p>';

const SIMPLE_HTML_OUTPUT_WITH_HIERARCHY = '<p>Note 2: <code>merchantUrl</code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/_publisher;path=publisherSystems>master>_Public_/publisherServices\')\">URL encoding</a>.</p>';

const COMPLEX_HTML_INPUT = '<p>Note 2: <code>merchantUrl <a href="thegardener://path=theGardener>master>_features_/administration">internal link1</a> </code> parameter must be encoded using <a href="thegardener://navigate/_publisher;path=publisherSystems>master>_Public_/publisherServices">internal link2</a>.</p>';

const COMPLEX_HTML_OUTPUT = '<p>Note 2: <code>merchantUrl <a onclick=\"navigateTo(\'app/documentation/navigate/HierarchyNode;path=theGardener>master>_features_/administration\')\">internal link1</a> </code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/_publisher;path=publisherSystems>master>_Public_/publisherServices\')\">internal link2</a>.</p>';

const HTML_INPUT_WITH_OTHER_LINK = '<p>Note 2: <code>merchantUrl <a href="/app/documentation/navigate/_publisher;path=shoppingAPI%3Eqa%3E_Tools_/requestBuilder">link</a></code> parameter must be encoded using <a href="thegardener://path=theGardener>master>_features_/administration">URL encoding</a>.</p>';

const HTML_OUTPUT_WITH_OTHER_LINK = '<p>Note 2: <code>merchantUrl <a href=\"/app/documentation/navigate/_publisher;path=shoppingAPI%3Eqa%3E_Tools_/requestBuilder\">link</a></code> parameter must be encoded using <a onclick=\"navigateTo(\'app/documentation/navigate/HierarchyNode;path=theGardener>master>_features_/administration\')\">URL encoding</a>.</p>';

