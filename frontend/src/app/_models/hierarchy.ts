export class BranchApi {
  name: string;
  features: Array<string>;
}

export class ProjectApi {
  id: string;
  label: string;
  stableBranch: string;
  branches: Array<BranchApi>;
}

export class HierarchyNodeApi {
  id: string;
  slugName: string;
  name: string;
  children?: Array<HierarchyNodeApi>;
  childrenLabel: string;
  childLabel: string;
  projects: Array<ProjectApi>;
}



