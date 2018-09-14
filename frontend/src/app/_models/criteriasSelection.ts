import {HierarchyNodeApi, ProjectApi} from "./criterias";
import {Params} from "@angular/router";
import {HttpParams} from "@angular/common/http";


class TupleHierarchyNodeSelector {

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

  public static buildHierarchyNodeSelector(apiResult: Array<HierarchyNodeApi>): Array<HierarchyNodeSelector> {
    var hierarchyNodeSelectorArray = new Array<HierarchyNodeSelector>();
    for (var i = 0; i < apiResult.length; i++) {
      var loopNodeApi = apiResult[i];
      var currentNodeSelector = HierarchyNodeSelector.newFromApi(loopNodeApi);
      hierarchyNodeSelectorArray.push(currentNodeSelector);
      if (loopNodeApi.projects != null) {
        for (var j = 0; j < loopNodeApi.projects.length; j++) {
          var loopProjectApi = loopNodeApi.projects[j];
          var currentProjectSelector = ProjectSelector.newFromApi(loopProjectApi);
          currentNodeSelector.projects.push(currentProjectSelector);
          currentProjectSelector.relatedHierarchyNode = currentNodeSelector;
        }
      }
    }
    return hierarchyNodeSelectorArray;
  }


  public static buildHierarchyNodeSelectorAsTree(listNode: Array<HierarchyNodeSelector>): HierarchyNodeSelector {
    var root: HierarchyNodeSelector = null;
    if (listNode.length > 0) {
      root = listNode[0];
      root.path = "";
      root.open = true;
      var tuple = HierarchyNodeSelector.build(root, listNode, root);
      root.children = tuple.taken;
    }
    return root;
  }


  private static build(node: HierarchyNodeSelector, children: Array<HierarchyNodeSelector>, root: HierarchyNodeSelector): TupleHierarchyNodeSelector {

    var taken = new Array<HierarchyNodeSelector>();
    var left = new Array<HierarchyNodeSelector>();

    if (children.length > 0) {
      for (var i = 0; i < children.length; i++) {
        var loopNode = children[i];
        if (loopNode.id.startsWith(node.id)) {
          loopNode.root = root;
          if (loopNode.id.length == node.id.length + 3) {
            loopNode.path = `${node.path}${CriteriasSelector.SEP_PATH}${loopNode.slugName}`;
            taken.push(loopNode);
          }
          if (loopNode.id.length > node.id.length + 3) {
            left.push(loopNode);
          }
        }
      }
    }
    if (taken.length > 0) {
      for (var i = 0; i < taken.length; i++) {
        var loopTaken = taken[i];
        var tuple = HierarchyNodeSelector.build(loopTaken, Object.assign([], left), root);
        loopTaken.children = tuple.taken;
      }
    }
    return new TupleHierarchyNodeSelector(taken, left);
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


  static SEP_PROJECT = ';';
  static SEP_PATH = '_';
  static SPLIT_PROJECT = '>';

  public humanizeHttpParams(projectsHttpParams: string): CriteriasDisplay {
    var criteriasDisplay = new CriteriasDisplay();

    if (projectsHttpParams != null) {
      var projectSelectorStringArray = projectsHttpParams.split(CriteriasSelector.SEP_PROJECT);
      for (var i = 0; i < projectSelectorStringArray.length; i++) {
        var projectSelectorString = projectSelectorStringArray[i];
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
    var projectHttpParamValue = "";
    for (var i = 0; i < this.hierarchyNodesSelector.length; i++) {
      var loopNode = this.hierarchyNodesSelector[i];
      var loopHttpParams = this._buildHttpParams(loopNode);
      if (loopHttpParams.length > 0) {
        if (projectHttpParamValue.length == 0) {
          projectHttpParamValue = loopHttpParams;
        } else {
          projectHttpParamValue = `${projectHttpParamValue}${CriteriasSelector.SEP_PROJECT}${loopHttpParams}`;
        }
      }
    }
    httpParams = httpParams.set("projects", projectHttpParamValue);
    return httpParams;

  }

  private _buildHttpParams(hierarchyNodeSelector: HierarchyNodeSelector): string {
    var httpParamsToReturn = "";

    if (!hierarchyNodeSelector.selected && !hierarchyNodeSelector.indeterminate) {
      return httpParamsToReturn;
    }
    if (hierarchyNodeSelector.selected) {
      httpParamsToReturn = hierarchyNodeSelector.path;
      var httpParamsForSpecificBranches = this._buildHttpParamsForSpecificBranches(hierarchyNodeSelector);
      if (httpParamsForSpecificBranches.length > 0) {
        httpParamsToReturn = `${httpParamsToReturn}${CriteriasSelector.SEP_PROJECT}${httpParamsForSpecificBranches}`
      }
      return httpParamsToReturn;
    }

    if (hierarchyNodeSelector.hasChilden()) {
      for (var i = 0; i < hierarchyNodeSelector.children.length; i++) {
        var loopNode = hierarchyNodeSelector.children[i];
        var loopHttpParams = this._buildHttpParams(loopNode);
        if (loopHttpParams.length > 0) {
          if (httpParamsToReturn.length == 0) {
            httpParamsToReturn = loopHttpParams;
          } else {
            httpParamsToReturn = `${httpParamsToReturn}${CriteriasSelector.SEP_PROJECT}${loopHttpParams}`
          }
        }
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
          if (httpParamsToReturn.length == 0) {
            httpParamsToReturn = loopHttpParams;
          } else {
            httpParamsToReturn = `${httpParamsToReturn};${loopHttpParams}`
          }
        }
      }
    }
    return httpParamsToReturn;
  }

  private _buildHttpParamsForSpecificBranches(hierarchyNodeSelector: HierarchyNodeSelector): string {
    var httpParamsToReturn = "";
    if (hierarchyNodeSelector.hasProjects()) {
      for (var i = 0; i < hierarchyNodeSelector.projects.length; i++) {
        var loopProject: ProjectSelector = hierarchyNodeSelector.projects[i];
        if (loopProject.selected) {
          if (loopProject.selectedBranch != null && loopProject.selectedBranch.name != loopProject.stableBranch.name) {
            var loopHttpParams = `${hierarchyNodeSelector.path}${CriteriasSelector.SPLIT_PROJECT}${loopProject.id}${CriteriasSelector.SPLIT_PROJECT}${loopProject.selectedBranch.name}`
            if (httpParamsToReturn.length == 0) {
              httpParamsToReturn = loopHttpParams;
            } else {
              httpParamsToReturn = `${httpParamsToReturn}${CriteriasSelector.SEP_PROJECT}${loopHttpParams}`
            }
          }
        }
      }
    }
    if (hierarchyNodeSelector.hasChilden()) {
      for (var i = 0; i < hierarchyNodeSelector.children.length; i++) {
        var loopNode = hierarchyNodeSelector.children[i];
        var loopHttpParams = this._buildHttpParamsForSpecificBranches(loopNode);
        if (loopHttpParams.length > 0) {
          if (httpParamsToReturn.length == 0) {
            httpParamsToReturn = loopHttpParams;
          } else {
            httpParamsToReturn = `${httpParamsToReturn}${CriteriasSelector.SEP_PROJECT}${loopHttpParams}`
          }
        }
      }
    }
    return httpParamsToReturn;
  }
}
