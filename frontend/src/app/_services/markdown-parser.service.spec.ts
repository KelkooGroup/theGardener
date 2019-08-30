import {TestBed} from '@angular/core/testing';

import {MarkdownParserService} from './markdown-parser.service';
import {
  PAGE_SERVICE_RESPONSE,
  PAGE_WITH_EXTERNAL_LINK_SERVICE_RESPONSE,
  PAGE_WITH_SCENARIO
} from '../_testUtils/test-data.spec';
import {ExternalLinkPart, MarkdownPart, ScenarioPart} from '../_models/hierarchy';

describe('MarkdownParserService', () => {
  let service: MarkdownParserService;

  beforeEach(() => TestBed.configureTestingModule({}));

  beforeEach(() => {
    service = TestBed.get(MarkdownParserService);
    expect(service).toBeTruthy();
  });

  it('should parse simple markdown', () => {
    const simpleMarkdown = PAGE_SERVICE_RESPONSE.markdown;
    const matchSimpleMarkdown = service.parseMarkdown(simpleMarkdown);
    expect(matchSimpleMarkdown).not.toBeNull();
    expect(matchSimpleMarkdown.length).toBe(1);
    expect(matchSimpleMarkdown[0].type).toBe('Markdown');
    expect((matchSimpleMarkdown[0] as MarkdownPart).markdown).toEqual(PAGE_SERVICE_RESPONSE.markdown);
  });

  it('should parse markdown for external link', () => {
    const markdownForExternalLink = PAGE_WITH_EXTERNAL_LINK_SERVICE_RESPONSE.markdown;
    const matchExternalLink = service.parseMarkdown(markdownForExternalLink);
    expect(matchExternalLink).not.toBeNull();
    expect(matchExternalLink.length).toBe(1);
    expect(matchExternalLink[0].type).toBe('ExternalLink');
    expect((matchExternalLink[0] as ExternalLinkPart).externalLink).toEqual('http://publisher.corp.kelkoo.net/docs/#/Contact%20Management/getContact');
  });

  it('should parse markdown including one scenario', () => {
    const markdownWithScenario = PAGE_WITH_SCENARIO.markdown;
    const matchMarkdownWithScenario = service.parseMarkdown(markdownWithScenario);
    expect(matchMarkdownWithScenario).not.toBeNull();
    expect(matchMarkdownWithScenario.length).toBe(2);
    expect(matchMarkdownWithScenario[0].type).toBe('Markdown');
    expect((matchMarkdownWithScenario[0] as MarkdownPart).markdown).toEqual('As a developer, it can be useful to know the current version of the application. Please use the following endpoint: ');
    expect(matchMarkdownWithScenario[1].type).toBe('Scenario');
    expect((matchMarkdownWithScenario[1] as ScenarioPart).scenarioSettings).toBeTruthy();
    expect((matchMarkdownWithScenario[1] as ScenarioPart).scenarioSettings.project).toEqual('shoppingApi');
    expect((matchMarkdownWithScenario[1] as ScenarioPart).scenarioSettings.branch).toEqual('${current.branch}');
    expect((matchMarkdownWithScenario[1] as ScenarioPart).scenarioSettings.feature).toEqual('api/public/ProvideMetaInformation.feature');
    expect((matchMarkdownWithScenario[1] as ScenarioPart).scenarioSettings.select.tags).toEqual(['@public_meta']);
  });
});
