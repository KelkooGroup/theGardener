import {PagePart} from './page';

export interface HierarchyNodeApi {
  id: string;
  hierarchy: string;
  slugName: string;
  name: string;
  childrenLabel: string;
  childLabel: string;
  children?: Array<HierarchyNodeApi>;
  projects?: Array<ProjectApi>;
  directory?: DirectoryApi;
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
