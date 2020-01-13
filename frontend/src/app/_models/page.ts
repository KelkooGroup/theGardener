import {Scenario} from './gherkin';
import {OpenApiModel} from "./openApi";

export interface Page {
  title: string;
  path: string;
  order: number;
  parts: Array<PagePart>;
}

export interface PagePart {
  type: 'markdown' | 'includeExternalPage' | 'scenarios' | 'openApi';
  data: MarkdownPart | IncludeExternalPagePart | ScenarioPart | OpenApiPart;
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

export interface OpenApiPart {
  openApi: OpenApiModel;
}
