


export interface NavigationParams {
  nodes?: string ;
  project?: string;
  branch?: string;
  directories?: string;
  page?: string;
}

export class NavigationRoute {
  nodes?: Array<string> ;
  project?: string;
  branch?: string;
  stableBranch?: string;
  directories?: Array<string>;
  page?: string;
}


export class FrontendPath {
  pathFromNodes?: string;
  nodesPath?: string;
}
export class BackendPath {
  pathFromProject?: string;
}
