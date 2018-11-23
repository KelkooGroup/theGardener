import {HttpParams} from '@angular/common/http';
import {HierarchyNodeApi, ProjectApi} from '../_models/criterias';


export class TupleHierarchyNodeSelector {

  constructor(
    public taken: Array<HierarchyNodeSelector>,
    public left: Array<HierarchyNodeSelector>
  ) {
  }
}

export class BranchSelector {

  constructor(
    public name: string,
    public project: ProjectSelector
  ) {
  }

  public selection() {
    this.project.selectedBranch = this;
  }

}

export class ProjectSelector {

  public selected = false;
  public selectedBranch: BranchSelector;
  public relatedHierarchyNode: HierarchyNodeSelector;

  constructor(
    public id: string,
    public label: string,
    public stableBranch: BranchSelector,
    public branches: Array<BranchSelector>,
  ) {
  }

  public static newFromApi(projectApi: ProjectApi): ProjectSelector {

    const branches = [];
    const stableBranch = new BranchSelector(projectApi.stableBranch, null);
    const instance = new ProjectSelector(
      projectApi.id,
      projectApi.label,
      stableBranch,
      branches
    );
    stableBranch.project = instance;
    if (projectApi.branches != null) {
      for (let k = 0; k < projectApi.branches.length; k++) {
        branches.push(new BranchSelector(projectApi.branches[k], instance));
      }
    }
    return instance;
  }

  public selection(selected: boolean) {
    this.selected = selected;
    this.relatedHierarchyNode.root.updateIndeterminateStatus();
  }
}

export class HierarchyNodeSelector {
  public selected = false;
  public indeterminate = false;
  public open = false;
  public level: number;
  public path: string;
  public children = [];
  public projects = [];
  public root: HierarchyNodeSelector;

  constructor(
    public id: string,
    public slugName: string,
    public name: string,
    public childrenLabel: string,
    public childLabel: string) {
    this.level = this.id.split('.').length - 1;
  }

  public static newFromApi(nodeApi: HierarchyNodeApi): HierarchyNodeSelector {
    return new HierarchyNodeSelector(
      nodeApi.id,
      nodeApi.slugName,
      nodeApi.name,
      nodeApi.childrenLabel,
      nodeApi.childLabel
    );
  }

  public hasChilden(): boolean {
    return this.children.length > 0;
  }

  public hasProjects(): boolean {
    return this.projects.length > 0;
  }

  public clone(): HierarchyNodeSelector {
    const instance = new HierarchyNodeSelector(
      this.id,
      this.slugName,
      this.name,
      this.childrenLabel,
      this.childLabel
    );
    instance.path = this.path;
    instance.children = this.children;
    instance.projects = this.projects;
    return instance;
  }

  public selection(selected: boolean) {
    this.selected = selected;
    for (let i = 0; i < this.children.length; i++) {
      this.children[i].selection(selected);
    }
    for (let i = 0; i < this.projects.length; i++) {
      this.projects[i].selected = this.selected;
    }
  }

  public refreshIndeterminateStatus() {
    this.root.updateIndeterminateStatus();
  }

  public updateIndeterminateStatus(): string {

    let localStatus = 'na';

    if (!this.hasChilden() && !this.hasProjects()) {
      this.indeterminate = false;
      return localStatus;
    }

    if (this.hasProjects()) {
      let nbSelected = 0;

      for (let j = 0; j < this.projects.length; j++) {
        const loopProjectApi = this.projects[j];
        if (loopProjectApi.selected) {
          nbSelected++;
        }
      }
      if (nbSelected === this.projects.length) {
        localStatus = 'selected';
      } else {
        if (nbSelected === 0) {
          localStatus = 'notSelected';
        } else {
          localStatus = 'indeterminate';
        }
      }
    }

    if (!this.hasChilden()) {
      this.indeterminate = (localStatus === 'indeterminate');
      this.selected = (localStatus === 'selected');
      return localStatus;
    }

    let status = 'na';

    let nbChildrenSelected = 0;
    let nbChildrenNotSelected = 0;
    let nbChildrenIndeterminate = 0;

    for (let i = 0; i < this.children.length; i++) {
      const loopNode = this.children[i];
      const loopStatus = loopNode.updateIndeterminateStatus();
      switch (loopStatus) {
        case 'notSelected' :
          nbChildrenNotSelected++;
          break;
        case 'indeterminate' :
          nbChildrenIndeterminate++;
          break;
        case 'selected' :
          nbChildrenSelected++;
          break;
      }
    }

    switch (localStatus) {
      case 'na' :
        if (nbChildrenIndeterminate > 0 || (nbChildrenSelected > 0 && nbChildrenNotSelected > 0)) {
          status = 'indeterminate';
        } else {
          if (nbChildrenNotSelected > 0) {
            status = 'notSelected';
          } else {
            status = 'selected';
          }
        }
        break;
      case 'selected' :
        if (nbChildrenIndeterminate > 0 || nbChildrenNotSelected > 0) {
          status = 'indeterminate';
        }
        break;
      case 'notSelected' :
        if (nbChildrenIndeterminate > 0 || nbChildrenSelected > 0) {
          status = 'indeterminate';
        }
        break;
      case 'indeterminate' :
        break;
    }

    this.indeterminate = (status === 'indeterminate');
    this.selected = (status === 'selected');
    return status;
  }
}


export class HierarchyNodeDisplay {

  constructor(
    public path: string,
    public humanizedPath: string
  ) {
  }

  public toString() {
    return this.humanizedPath;
  }

}

export class ProjectDisplay {

  public specificBranch: string;

  constructor(
    public node: HierarchyNodeDisplay,
    public id: string,
    public name: string,
  ) {
  }

  public toString() {
    const project = `On ${this.node} only ${this.name}`;
    if (this.specificBranch) {
      return `${project} at ${this.specificBranch } branch`;
    } else {
      return project;
    }
  }
}

export class CriteriasDisplay {

  projects = [];
  hierarchyNodes = [];
}

export class CriteriasSelector {

  static PARAM_PROJECT = 'project';
  static PARAM_NODE = 'node';

  static SEP_PATH = '_';
  static SPLIT_PROJECT = '>';

  public hierarchyNodesSelector: HierarchyNodeSelector[];

  humanizeHttpParams(nodes: string[], projects: string[]): CriteriasDisplay {
    const criteriasDisplay = new CriteriasDisplay();


    if (nodes != null) {
      for (let i = 0; i < nodes.length; i++) {
        const path = nodes[i];
        const hierarchyNode = new HierarchyNodeDisplay(path, this.humanizeNodePath(path));
        criteriasDisplay.hierarchyNodes.push(hierarchyNode);
      }
    }
    if (projects != null) {
      for (let i = 0; i < projects.length; i++) {
        const projectSelectorString = projects[i];
        if (projectSelectorString != null) {
          const projectSelectorParams = projectSelectorString.split(CriteriasSelector.SPLIT_PROJECT);

          if (projectSelectorParams.length > 0) {
            const path = projectSelectorParams[0];
            const hierarchyNode = new HierarchyNodeDisplay(path, this.humanizeNodePath(path));
            if (projectSelectorParams.length === 1) {
              criteriasDisplay.hierarchyNodes.push(hierarchyNode);
            } else {
              const projectParam = projectSelectorParams[1];
              const project = new ProjectDisplay(hierarchyNode, projectParam, this.humanizeProjectId(projectParam));
              if (projectSelectorParams.length > 2) {
                project.specificBranch = projectSelectorParams[2];
              }
              criteriasDisplay.projects.push(project);
            }
          }
        }
      }
    }

    return criteriasDisplay;
  }

  public humanizeNodePath(nodePath: string): string {

    return nodePath;
  }

  public humanizeProjectId(projectId: string): string {

    return projectId;
  }

  public buildHttpParams(): HttpParams {

    let httpParams = new HttpParams();
    for (let i = 0; i < this.hierarchyNodesSelector.length; i++) {
      const loopNode = this.hierarchyNodesSelector[i];
      httpParams = this._buildHttpParams(httpParams, loopNode);
    }
    return httpParams;

  }

  private _buildHttpParams(httpParams: HttpParams, hierarchyNodeSelector: HierarchyNodeSelector): HttpParams {

    if (!hierarchyNodeSelector.selected && !hierarchyNodeSelector.indeterminate) {
      return httpParams;
    }
    if (hierarchyNodeSelector.selected) {
      httpParams = httpParams.append(CriteriasSelector.PARAM_NODE, hierarchyNodeSelector.path);
      return this._buildHttpParamsForSpecificBranches(httpParams, hierarchyNodeSelector);
    }

    if (hierarchyNodeSelector.hasChilden()) {
      for (let i = 0; i < hierarchyNodeSelector.children.length; i++) {
        httpParams = this._buildHttpParams(httpParams, hierarchyNodeSelector.children[i]);
      }
    }
    if (hierarchyNodeSelector.hasProjects()) {
      for (let i = 0; i < hierarchyNodeSelector.projects.length; i++) {
        const loopProject: ProjectSelector = hierarchyNodeSelector.projects[i];
        if (loopProject.selected) {
          let loopHttpParams = `${hierarchyNodeSelector.path}${CriteriasSelector.SPLIT_PROJECT}${loopProject.id}`;
          if (loopProject.selectedBranch !== null && loopProject.selectedBranch.name !== loopProject.stableBranch.name) {
            loopHttpParams += `${CriteriasSelector.SPLIT_PROJECT}${loopProject.selectedBranch.name}`;
          }
          httpParams = httpParams.append(CriteriasSelector.PARAM_PROJECT, loopHttpParams);
        }
      }
    }
    return httpParams;
  }

  private _buildHttpParamsForSpecificBranches(httpParams: HttpParams, hierarchyNodeSelector: HierarchyNodeSelector): HttpParams {
    if (hierarchyNodeSelector.hasProjects()) {
      for (let i = 0; i < hierarchyNodeSelector.projects.length; i++) {
        const loopProject: ProjectSelector = hierarchyNodeSelector.projects[i];
        if (loopProject.selected) {
          if (loopProject.selectedBranch != null && loopProject.selectedBranch.name !== loopProject.stableBranch.name) {
            const loopHttpParams = `${hierarchyNodeSelector.path}${CriteriasSelector.SPLIT_PROJECT}${loopProject.id}${CriteriasSelector.SPLIT_PROJECT}${loopProject.selectedBranch.name}`;
            httpParams = httpParams.append(CriteriasSelector.PARAM_PROJECT, loopHttpParams);
          }
        }
      }
    }
    if (hierarchyNodeSelector.hasChilden()) {
      for (let i = 0; i < hierarchyNodeSelector.children.length; i++) {
        httpParams = this._buildHttpParamsForSpecificBranches(httpParams, hierarchyNodeSelector.children[i]);
      }
    }
    return httpParams;
  }
}
