
import {HttpParams} from "@angular/common/http";
import {HierarchyNodeApi, ProjectApi} from "../_models/criterias";


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

  public selected: boolean = false;
  public selectedBranch: BranchSelector;
  public relatedHierarchyNode: HierarchyNodeSelector;

  constructor(
    public id: string,
    public label: string,
    public stableBranch: BranchSelector,
    public branches: Array<BranchSelector>,
  ) {
  }

  public selection(selected: boolean) {
    this.selected = selected;
    this.relatedHierarchyNode.root.updateIndeterminateStatus();
  }

  public static newFromApi(projectApi: ProjectApi): ProjectSelector {

    var branches = new Array<BranchSelector>();
    var stableBranch = new BranchSelector(projectApi.stableBranch, null);
    var instance = new ProjectSelector(
      projectApi.id,
      projectApi.label,
      stableBranch,
      branches
    );
    stableBranch.project = instance;
    if (projectApi.branches != null) {
      for (var k = 0; k < projectApi.branches.length; k++) {
        branches.push(new BranchSelector(projectApi.branches[k], instance));
      }
    }
    return instance;
  }

}

export class HierarchyNodeSelector {


  public selected: boolean = false;
  public indeterminate: boolean = false;
  public open: boolean = false;
  public level: number;
  public path: string;
  public children = new Array<HierarchyNodeSelector>();
  public projects = new Array<ProjectSelector>();
  public root: HierarchyNodeSelector;

  constructor(
    public id: string,
    public slugName: string,
    public name: string,
    public childrenLabel: string,
    public childLabel: string) {
    this.level = this.id.split(".").length - 1;
  }

  public hasChilden(): boolean {
    return this.children.length > 0;
  }

  public hasProjects(): boolean {
    return this.projects.length > 0;
  }

  public clone(): HierarchyNodeSelector {
    var instance = new HierarchyNodeSelector(
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
    for (var i = 0; i < this.children.length; i++) {
      this.children[i].selection(selected);
    }
    for (var i = 0; i < this.projects.length; i++) {
      this.projects[i].selected = this.selected;
    }
  }

  public refreshIndeterminateStatus() {
    this.root.updateIndeterminateStatus();
  }

  public updateIndeterminateStatus(): string {

    var localStatus: string = "na";

    if (!this.hasChilden() && !this.hasProjects()) {
      this.indeterminate = false;
      return localStatus;
    }

    if (this.hasProjects()) {
      var nbSelected = 0;

      for (var j = 0; j < this.projects.length; j++) {
        var loopProjectApi = this.projects[j];
        if (loopProjectApi.selected) {
          nbSelected++;
        }
      }
      if (nbSelected == this.projects.length) {
        localStatus = "selected";
      } else {
        if (nbSelected == 0) {
          localStatus = "notSelected";
        } else {
          localStatus = "indeterminate";
        }
      }
    }

    if (!this.hasChilden()) {
      this.indeterminate = (localStatus == "indeterminate");
      this.selected = (localStatus == "selected");
      return localStatus;
    }

    var status = "na";

    var nbChildrenSelected = 0;
    var nbChildrenNotSelected = 0;
    var nbChildrenIndeterminate = 0;

    for (var i = 0; i < this.children.length; i++) {
      var loopNode = this.children[i];
      var loopStatus = loopNode.updateIndeterminateStatus();
      switch (loopStatus) {
        case "notSelected" :
          nbChildrenNotSelected++;
          break;
        case "indeterminate" :
          nbChildrenIndeterminate++;
          break;
        case "selected" :
          nbChildrenSelected++;
          break;
      }
    }

    switch (localStatus) {
      case "na" :
        if (nbChildrenIndeterminate > 0 || (nbChildrenSelected > 0 && nbChildrenNotSelected > 0)) {
          status = "indeterminate";
        } else {
          if (nbChildrenNotSelected > 0) {
            status = "notSelected";
          } else {
            status = "selected";
          }
        }
        break;
      case "selected" :
        if (nbChildrenIndeterminate > 0 || nbChildrenNotSelected > 0) {
          status = "indeterminate";
        }
        break;
      case "notSelected" :
        if (nbChildrenIndeterminate > 0 || nbChildrenSelected > 0) {
          status = "indeterminate";
        }
        break;
      case "indeterminate" :
        break;
    }

    this.indeterminate = (status == "indeterminate");
    this.selected = (status == "selected");
    return status;
  }


  public static newFromApi(nodeApi: HierarchyNodeApi): HierarchyNodeSelector {
    var instance = new HierarchyNodeSelector(
      nodeApi.id,
      nodeApi.slugName,
      nodeApi.name,
      nodeApi.childrenLabel,
      nodeApi.childLabel
    );
    return instance;
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
    var project = `On ${this.node} only ${this.name}`;
    if (this.specificBranch) {
      return `${project} at ${this.specificBranch } branch`;
    } else {
      return project;
    }
  }
}

export class CriteriasDisplay {

  projects = new Array<ProjectDisplay>();
  hierarchyNodes = new Array<HierarchyNodeDisplay>();
}

export class CriteriasSelector {

  public hierarchyNodesSelector: HierarchyNodeSelector[];


  static PARAM_PROJECT = 'project';
  static PARAM_NODE = 'node';

  static SEP_PATH = '_';
  static SPLIT_PROJECT = '>';

  public humanizeHttpParams(nodes: string[], projects: string[]): CriteriasDisplay {
    var criteriasDisplay = new CriteriasDisplay();


    if (nodes != null) {
      for (var i = 0; i < nodes.length; i++) {
        var path = nodes[i];
        var hierarchyNode = new HierarchyNodeDisplay(path, this.humanizeNodePath(path));
        criteriasDisplay.hierarchyNodes.push(hierarchyNode);
      }
    }
    if (projects != null) {
      for (var i = 0; i < projects.length; i++) {
        var projectSelectorString = projects[i];
        if (projectSelectorString != null) {
          var projectSelectorParams = projectSelectorString.split(CriteriasSelector.SPLIT_PROJECT);

          if (projectSelectorParams.length > 0) {
            var path = projectSelectorParams[0];
            var hierarchyNode = new HierarchyNodeDisplay(path, this.humanizeNodePath(path));
            if (projectSelectorParams.length == 1) {
              criteriasDisplay.hierarchyNodes.push(hierarchyNode);
            } else {
              var projectParam = projectSelectorParams[1];
              var project = new ProjectDisplay(hierarchyNode, projectParam, this.humanizeProjectId(projectParam));
              if (projectSelectorParams.length > 2) {
                var projectBranch = projectSelectorParams[2];
                project.specificBranch = projectBranch;
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

    var httpParams = new HttpParams()
    for (var i = 0; i < this.hierarchyNodesSelector.length; i++) {
      var loopNode = this.hierarchyNodesSelector[i];
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
      for (var i = 0; i < hierarchyNodeSelector.children.length; i++) {
        httpParams = this._buildHttpParams(httpParams, hierarchyNodeSelector.children[i]);
      }
    }
    if (hierarchyNodeSelector.hasProjects()) {
      for (var i = 0; i < hierarchyNodeSelector.projects.length; i++) {
        var loopProject: ProjectSelector = hierarchyNodeSelector.projects[i];
        if (loopProject.selected) {
          var loopHttpParams = `${hierarchyNodeSelector.path}${CriteriasSelector.SPLIT_PROJECT}${loopProject.id}`;
          if (loopProject.selectedBranch != null && loopProject.selectedBranch.name != loopProject.stableBranch.name) {
            loopHttpParams += `${CriteriasSelector.SPLIT_PROJECT}${loopProject.selectedBranch.name}`
          }
          httpParams = httpParams.append(CriteriasSelector.PARAM_PROJECT, loopHttpParams);
        }
      }
    }
    return httpParams;
  }

  private _buildHttpParamsForSpecificBranches(httpParams: HttpParams, hierarchyNodeSelector: HierarchyNodeSelector): HttpParams {
    if (hierarchyNodeSelector.hasProjects()) {
      for (var i = 0; i < hierarchyNodeSelector.projects.length; i++) {
        var loopProject: ProjectSelector = hierarchyNodeSelector.projects[i];
        if (loopProject.selected) {
          if (loopProject.selectedBranch != null && loopProject.selectedBranch.name != loopProject.stableBranch.name) {
            var loopHttpParams = `${hierarchyNodeSelector.path}${CriteriasSelector.SPLIT_PROJECT}${loopProject.id}${CriteriasSelector.SPLIT_PROJECT}${loopProject.selectedBranch.name}`
            httpParams = httpParams.append(CriteriasSelector.PARAM_PROJECT, loopHttpParams);
          }
        }
      }
    }
    if (hierarchyNodeSelector.hasChilden()) {
      for (var i = 0; i < hierarchyNodeSelector.children.length; i++) {
        httpParams = this._buildHttpParamsForSpecificBranches(httpParams, hierarchyNodeSelector.children[i]);
      }
    }
    return httpParams;
  }
}
