import {Scenario} from './gherkin';

export interface Page {
  title: string;
  path: string;
  order: number;
  parts: Array<PagePart>;
}

export interface PagePart {
  type: 'markdown' | 'externalLink' | 'scenarios';
  data: MarkdownPart | ExternalLinkPart | ScenarioPart;
}

export interface MarkdownPart {
  markdown: string;
}

export interface ExternalLinkPart {
  externalLink: string;
}

export interface ScenarioPart {
  scenarios: Scenario;
}
