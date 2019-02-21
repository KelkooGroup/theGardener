import {HttpParams} from '@angular/common/http';
import {HierarchyNodeApi, ProjectApi} from '../_models/criterias';


export class TupleHierarchyNodeSelector {

  constructor(
    public taken: Array<HierarchyNodeSelector>,
    public left: Array<HierarchyNodeSelector>
  ) {
  }
}

export class FeatureSelector {
  constructor(
    public level: number,
    public value: string,
    public display: string,
  ) {

  }

}

const NO_FEATURE_FILTER = new FeatureSelector(0, '~', 'None features');
const ALL_FEATURES_FILTER = new FeatureSelector(0, '*', 'All features');

export class BranchSelector {

  featureFilter: FeatureSelector;
  features: Array<FeatureSelector>;

  constructor(
    public name: string,
    public featureRowPath: Array<string>,
    public project: ProjectSelector
  ) {
    this.features = new Array<FeatureSelector>();
    this.featureFilter = NO_FEATURE_FILTER;
    this.features.push(ALL_FEATURES_FILTER);
    let lastDirectory = '';
    for (const currentPath of featureRowPath) {
      const split = currentPath.split('/');
      if (split.length === 1) {
        this.features.push(new FeatureSelector(split.length, currentPath, currentPath));
      } else {
        if (split.length === 2) {
          const currentDirectory = split[0];
          if (currentDirectory !== lastDirectory) {
            this.features.push(new FeatureSelector(split.length - 1, currentDirectory, currentDirectory + ' :'));
            lastDirectory = currentDirectory;
          }
          this.features.push(new FeatureSelector(split.length, currentPath, split[1]));
        } else {
          this.features.push(new FeatureSelector(split.length, currentPath, currentPath));
        }
      }
    }
  }

  selectBranch() {
    this.project.branchSelection(this);
    this.selectAllFeatures();
  }

  selectFeature(feature: FeatureSelector) {
    this.project.featureSelection(feature);
  }

  selectAllFeatures() {
    this.featureFilter = ALL_FEATURES_FILTER;
  }

  selectNoneFeatures() {
    this.featureFilter = NO_FEATURE_FILTER;
  }

  hasFilter(): boolean {
    return this.featureFilter !== ALL_FEATURES_FILTER && this.featureFilter !== NO_FEATURE_FILTER;
  }
}

export class ProjectSelector {

  selected = false;
  indeterminate = false;
  selectedBranch: BranchSelector;
  stableBranch: BranchSelector;
  relatedHierarchyNode: HierarchyNodeSelector;

  constructor(
    public id: string,
    public label: string,
    public branches: Array<BranchSelector>,
  ) {
  }

  static newFromApi(projectApi: ProjectApi): ProjectSelector {

    const branches: Array<BranchSelector> = [];
    const mapBranchNameBranch = new Map();
    const instance = new ProjectSelector(
      projectApi.id,
      projectApi.label,
      branches
    );
    if (projectApi.branches !== null) {
      for (const branch of projectApi.branches) {
        const currentBranch = new BranchSelector(branch.name, branch.features, instance);
        branches.push(currentBranch);
        mapBranchNameBranch.set(currentBranch.name, currentBranch);
      }
    }

    instance.stableBranch = mapBranchNameBranch.get(projectApi.stableBranch);
    instance.stableBranch.project = instance;
    instance.selectedBranch = instance.stableBranch;
    return instance;
  }

  selection(selected: boolean) {
    this.selected = selected;
    this.selectedBranch = this.stableBranch;
    if (this.selected) {
      this.selectedBranch.selectAllFeatures();
    } else {
      this.selectedBranch.selectNoneFeatures();
    }
    this.relatedHierarchyNode.root.updateIndeterminateStatus();
  }

  featureSelection(selectedFeature: FeatureSelector) {
    this.selected = true;
    this.selectedBranch.featureFilter = selectedFeature;
    this.relatedHierarchyNode.root.updateIndeterminateStatus();
  }

  branchSelection(selectedBranch: BranchSelector) {
    this.selected = true;
    this.selectedBranch = selectedBranch;
    this.relatedHierarchyNode.root.updateIndeterminateStatus();
  }

  isIndeterminate(): boolean {

    this.indeterminate = !this.selectedBranch || (this.selectedBranch && this.selectedBranch.name !== this.stableBranch.name);
    if (this.selectedBranch && this.selectedBranch.hasFilter()) {
      this.indeterminate = true;
    }

    return this.indeterminate;
  }


}

export class HierarchyNodeSelector {
  selected = false;
  indeterminate = false;
  open = false;
  level: number;
  path: string;
  children: Array<HierarchyNodeSelector> = [];
  projects: Array<ProjectSelector> = [];
  root: HierarchyNodeSelector;

  constructor(
    public id: string,
    public slugName: string,
    public name: string,
    public childrenLabel: string,
    public childLabel: string) {
    this.level = this.id.split('.').length - 1;
  }

  static newFromApi(nodeApi: HierarchyNodeApi): HierarchyNodeSelector {
    return new HierarchyNodeSelector(
      nodeApi.id,
      nodeApi.slugName,
      nodeApi.name,
      nodeApi.childrenLabel,
      nodeApi.childLabel
    );
  }

  hasChilden(): boolean {
    return this.children.length > 0;
  }

  hasProjects(): boolean {
    return this.projects.length > 0;
  }

  clone(): HierarchyNodeSelector {
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

  selection(selected: boolean) {
    this.selected = selected;
    for (const child of this.children) {
      child.selection(selected);
    }
    for (const project of this.projects) {
      project.selected = this.selected;
    }
  }

  refreshIndeterminateStatus() {
    this.root.updateIndeterminateStatus();
  }

  updateIndeterminateStatus(): string {

    let localStatus = 'na';

    if (!this.hasChilden() && !this.hasProjects()) {
      this.indeterminate = false;
      return localStatus;
    }

    if (this.hasProjects()) {
      let nbSelected = 0;
      let nbIndeterminate = 0;

      for (const loopProjectApi of this.projects) {
        if (loopProjectApi.selected) {
          nbSelected++;
        }
        if (loopProjectApi.isIndeterminate()) {
          nbIndeterminate++;
        }
      }
      if (nbSelected === this.projects.length && nbIndeterminate === 0) {
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

    for (const loopNode of this.children) {
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

  toString() {
    return this.humanizedPath;
  }

}

export class ProjectDisplay {

  specificBranch: string;

  constructor(
    public node: HierarchyNodeDisplay,
    public id: string,
    public name: string,
  ) {
  }

  toString() {
    const project = `On ${this.node} only ${this.name}`;
    if (this.specificBranch) {
      return `${project} at ${this.specificBranch} branch`;
    } else {
      return project;
    }
  }
}

export class CriteriasDisplay {

  projects: Array<ProjectDisplay> = [];
  hierarchyNodes: Array<HierarchyNodeDisplay> = [];
}

export class CriteriasSelector {

  static PARAM_PROJECT = 'project';
  static PARAM_NODE = 'node';

  static SEP_PATH = '_';
  static SPLIT_PROJECT = '>';

  hierarchyNodesSelector: Array<HierarchyNodeSelector>;

  humanizeHttpParams(nodes: Array<string>, projects: Array<string>): CriteriasDisplay {
    const criteriasDisplay = new CriteriasDisplay();


    if (nodes !== undefined) {
      for (const path of nodes) {
        const hierarchyNode = new HierarchyNodeDisplay(path, this.humanizeNodePath(path));
        criteriasDisplay.hierarchyNodes.push(hierarchyNode);
      }
    }
    if (projects !== undefined) {
      for (const projectSelectorString of projects) {
        if (projectSelectorString !== undefined) {
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

  humanizeNodePath(nodePath: string): string {

    return nodePath;
  }

  humanizeProjectId(projectId: string): string {

    return projectId;
  }

  buildHttpParams(): HttpParams {

    let httpParams = new HttpParams();
    for (const loopNode of this.hierarchyNodesSelector) {
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
      return this._buildHttpParamsForSpecificProjectSelection(httpParams, hierarchyNodeSelector);
    }

    if (hierarchyNodeSelector.hasChilden()) {
      for (const child of hierarchyNodeSelector.children) {
        httpParams = this._buildHttpParams(httpParams, child);
      }
    }
    if (hierarchyNodeSelector.hasProjects()) {
      for (const loopProject of hierarchyNodeSelector.projects) {
        if (loopProject.selected) {
          const loopHttpParams = this._buildHttpParamsForAProject(loopProject);
          httpParams = httpParams.append(CriteriasSelector.PARAM_PROJECT, loopHttpParams);
        }
      }
    }
    return httpParams;
  }

  private _buildHttpParamsForSpecificProjectSelection(httpParams: HttpParams, hierarchyNodeSelector: HierarchyNodeSelector): HttpParams {
    if (hierarchyNodeSelector.hasProjects()) {
      for (const loopProject of hierarchyNodeSelector.projects) {
        if (loopProject.selected) {
          if (loopProject.selectedBranch !== undefined
            && (
              (loopProject.selectedBranch.name !== loopProject.stableBranch.name)
              || (loopProject.selectedBranch.featureFilter.value !== ALL_FEATURES_FILTER.value)
            )
          ) {
            const loopHttpParams = this._buildHttpParamsForAProject(loopProject);
            httpParams = httpParams.append(CriteriasSelector.PARAM_PROJECT, loopHttpParams);
          }
        }
      }
    }
    if (hierarchyNodeSelector.hasChilden()) {
      for (const child of hierarchyNodeSelector.children) {
        httpParams = this._buildHttpParamsForSpecificProjectSelection(httpParams, child);
      }
    }
    return httpParams;
  }


  private _buildHttpParamsForAProject(project: ProjectSelector): string {
    let projectHttpParams = `${project.relatedHierarchyNode.path}${CriteriasSelector.SPLIT_PROJECT}${project.id}`;
    const selectedBranch = project.selectedBranch;
    if (selectedBranch !== undefined) {
      projectHttpParams += `${CriteriasSelector.SPLIT_PROJECT}${project.selectedBranch.name}`;
      if (selectedBranch.featureFilter !== undefined) {
        projectHttpParams += `${CriteriasSelector.SPLIT_PROJECT}${selectedBranch.featureFilter.value}`;
      }
    }
    return projectHttpParams;
  }
}
