export interface HierarchyNodeApi {
  id: string;
  hierarchy: string;
  slugName: string;
  name: string;
  childrenLabel: string;
  childLabel: string;
  children?: Array<HierarchyNodeApi>;
  projects?: Array<ProjectApi>;
}

export interface ProjectApi {
  id: string;
  label: string;
  path: string;
  stableBranch: string;
  branches: Array<BranchApi>;
}

export interface BranchApi {
  name: string;
  path: string;
  rootDirectory?: DirectoryApi;
}

export interface DirectoryApi {
  id: string;
  path: string;
  name: string;
  label: string;
  description: string;
  order: number;
  pages?: Array<PageApi>;
  children?: Array<DirectoryApi>;
}

export interface PageApi {
  path: string;
  relativePath: string;
  name: string;
  label: string;
  description: string;
  order: number;
  content?: Array<PagePart>;
}

export interface Page {
  title: string;
  path: string;
  order: number;
  parts: Array<PagePart>;
}

export interface MarkdownSettings {
  include?: PageIncludeSettings;
  scenarios?: ScenarioIncludeSettings;
}

export interface PageIncludeSettings {
  type: string;
  url: string;
}

export interface ScenarioIncludeSettings {
  project: string;
  branch: string;
  feature: string;
  select: ScenarioFilterSettings;
}

export interface ScenarioFilterSettings {
  tags: Array<string>;
}

export interface PagePart {
  type: 'Markdown' | 'ExternalLink' | 'Scenarios';
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

export interface Scenario {
  id: string;
  branchId: string;
  path: string;
  background?: GherkinBackground;
  tags: Array<string>;
  language: string;
  keyword: string; // FIXME
  name: string;
  description: string;
  scenarios: Array<GherkinScenario>;
  comments: Array<string>; // FIXME: do we keep it ?
}

export interface GherkinBackground {
  id: string;
  keyword: string; // FIXME (is it an enum ?)
  name: string; // FIXME: do we keep it ?
  description: string; // FIXME: do we keep it ?
  steps: Array<GherkinStep>;

}

export interface GherkinScenario {
  id: string;
  keyword: string; // FIXME: do we keep it ?
  name: string;
  description: string;
  tags: Array<string>;
  abstractionLevel: string;
  caseType: string;
  steps: Array<GherkinStep>;
  examples?: GherkinTable;
  workflowStep: string; // FIXME: any meaning on frontend side ?
}

export interface GherkinStep {
  id: string;
  keyword: string; // FIXME (is it an enum ?)
  text: string;
  argument: Array<Array<string>>;
}

export interface GherkinTable {
  tableHeader: Array<string>;
  tableBody: Array<Array<string>>;
}

export interface GherkinTextFragment {
  text: string;
  isParameter: boolean;
}

