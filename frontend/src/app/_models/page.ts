import {Scenario} from './gherkin';
import {OpenApiModel, OpenApiPath} from './open-api';

export interface Page {
  title: string;
  path: string;
  order: number;
  parts: Array<PagePart>;
}

export interface PagePart {
  type: 'markdown' | 'includeExternalPage' | 'scenarios' | 'openApi' | 'openApiPath';
  data: MarkdownPart | IncludeExternalPagePart | ScenarioPart | OpenApiPart | OpenApiPathsPart;
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

export interface OpenApiPathsPart{
  openApiPath: OpenApiPath;
}
