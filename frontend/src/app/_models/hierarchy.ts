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
  markdown?: string;
}

export interface Page {
  title: string;
  path: string;
  order: number;
  parts: Array<PagePart>;
}

export interface MarkdownSettings  {
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
  type: 'Markdown' | 'ExternalLink' | 'Scenario';
}

export interface MarkdownPart extends PagePart {
  markdown: string;
}

export interface ExternalLinkPart extends PagePart {
  externalLink: string;
}

export interface ScenarioPart extends PagePart {
  scenarioSettings: ScenarioIncludeSettings;
}
