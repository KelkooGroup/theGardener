import {Scenario} from './gherkin';

export interface Page {
  title: string;
  path: string;
  order: number;
  parts: Array<PagePart>;
}

export interface PagePart {
  type: 'markdown' | 'includeExternalPage' | 'scenarios';
  data: MarkdownPart | IncludeExternalPagePart | ScenarioPart;
}

export interface MarkdownPart {
  markdown: string;
}

export interface IncludeExternalPagePart {
  includeExternalPage: string;
}

export interface ScenarioPart {
  scenarios: Scenario;
}
