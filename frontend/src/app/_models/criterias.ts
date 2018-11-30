export class BranchApi {
  public name: string;
  public features: Array<string>;
}

export class ProjectApi {
  public id: string;
  public label: string;
  public stableBranch: string;
  public branches: Array<BranchApi>;
}

export class HierarchyNodeApi {
  public id: string;
  public slugName: string;
  public name: string;
  public children: Array<HierarchyNodeApi>;
  public childrenLabel: string;
  public childLabel: string;
  public projects: Array<ProjectApi>;
}



