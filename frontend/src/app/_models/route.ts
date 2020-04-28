


export interface NavigationParams {
  nodes?: string ;
  project?: string;
  branch?: string;
  directories?: string;
  page?: string;
}

export interface NavigationRoute {
  nodes?: Array<string> ;
  project?: string;
  branch?: string;
  stableBranch?: string;
  directories?: Array<string>;
  page?: string;
}


export interface FrontendPath {
  pathFromNodes?: string;
  nodesPath?: string;
}

export interface BackendPath {
  pathFromProject?: string;
}
