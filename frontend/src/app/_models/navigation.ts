import {HierarchyNodeApi, ProjectApi} from './hierarchy';

export interface NavigationWithOptions {
  itemOptions: boolean;
  itemOptionPlaceHolder?: string;
  itemOptionSelected(): NavigationItem;

}

export interface NavigationItem extends  NavigationWithOptions {
  selected: boolean;
  itemType: string;
  displayName: string;
  route?: string;
  toBeDisplayed: boolean;
  matchPage(page: string): boolean;
  itemChildren(): Array<NavigationItem>;
}

export class NavigationFeature implements NavigationItem {
  selected = false ;
  itemOptions = false ;
  toBeDisplayed = true;
  constructor(
    public level: number,
    public value: string,
    public display: string,
    public directory: NavigationDirectory
  ) {

  }

  matchPage(page: string): boolean {
    return page &&  page.startsWith(this.route);
  }

  path() {
    return this.directory.path() + '/' + this.value ;
  }

  get displayName(): string {
    return this.display;
  }

  get itemType(): string {
    return 'feature';
  }

  get route(): string {
    return this.path();
  }

  itemChildren(): Array<NavigationItem> {
    return Array<NavigationItem>();
  }

  itemOptionSelected(): NavigationItem {
    return null;
  }

}

export class NavigationDirectory implements NavigationItem {

  features: Array<NavigationFeature>;
  directories: Array<NavigationDirectory>;
  selected = false ;
  itemOptions = false ;
  toBeDisplayed = false;

  constructor(
    public name: string,
    featureRowPath: Array<string>,
    public pathFromRoot: string,
    public branch: NavigationBranch
  ) {
    this.features = new Array<NavigationFeature>();
    this.directories = new Array<NavigationDirectory>();
    let lastDirectory = '';
    let lastDirectoryFeatureRowPath = new Array<string>();
    for (const currentPath of featureRowPath) {
      const split = currentPath.split('/', 2);
      if (split.length === 1) {
        this.features.push(new NavigationFeature(split.length, currentPath, currentPath.replace('.feature', ''), this ));
      } else {
        if (split.length === 2) {
          const currentDirectory = split[0];
          if (currentDirectory !== lastDirectory ) {
            if ( lastDirectory !== '') {
                this.directories.push(new NavigationDirectory(lastDirectory,  lastDirectoryFeatureRowPath, pathFromRoot + '/' + lastDirectory, branch));
            }
            lastDirectory = currentDirectory;
            lastDirectoryFeatureRowPath = new Array<string>();
          }
          lastDirectoryFeatureRowPath.push( split[1]);
        }
      }
    }
    if ( lastDirectory !== '') {
       this.directories.push(new NavigationDirectory(lastDirectory,  lastDirectoryFeatureRowPath, pathFromRoot + '/' + lastDirectory, branch));
    }



  }

  matchPage(page: string): boolean {
    return page &&  page.startsWith(this.route);
  }

  path() {
    return this.branch.path() + '>' + this.pathFromRoot ;
  }


  get displayName(): string {
    return this.name;
  }

  get itemType(): string {
    return 'directory';
  }

  itemChildren(): Array<NavigationItem> {
    const children = Array<NavigationItem>();
    this.directories.forEach(d => children.push(d));
    this.features.forEach(d => children.push(d));
    return children;
  }

  get route(): string {
    return this.path();
  }

  itemOptionSelected(): NavigationItem {
    return null;
  }
}

export class NavigationBranch implements NavigationItem {

  selected = false ;
  featureFilter: NavigationFeature;
  features: Array<NavigationFeature>;
  rootDirectory: NavigationDirectory;
  itemOptions = false ;
  toBeDisplayed = false;
  constructor(
    public name: string,
    public featureRowPath: Array<string>,
    public project: NavigationProject
  ) {
    this.rootDirectory  = new NavigationDirectory(name, featureRowPath, '', this);
    this.features = new Array<NavigationFeature>();
    let lastDirectory = '';
    for (const currentPath of featureRowPath) {
      const split = currentPath.split('/');
      if (split.length === 1) {
        this.features.push(new NavigationFeature(split.length, currentPath, currentPath, this.rootDirectory));
      } else {
        if (split.length === 2) {
          const currentDirectory = split[0];
          if (currentDirectory !== lastDirectory) {
            this.features.push(new NavigationFeature(split.length - 1, currentDirectory, currentDirectory + ' :', this.rootDirectory));
            lastDirectory = currentDirectory;
          }
          this.features.push(new NavigationFeature(split.length, currentPath, split[1], this.rootDirectory));
        } else {
          this.features.push(new NavigationFeature(split.length, currentPath, currentPath, this.rootDirectory));
        }
      }
    }
  }

  path() {
    return this.project.path() + '>' + this.name ;
  }

  get displayName(): string {
    return this.name;
  }

  get itemType(): string {
    return 'directory';
  }

  itemChildren(): Array<NavigationItem> {
    return this.rootDirectory.itemChildren();
  }

  get route(): string {
    return this.path();
  }

  itemOptionSelected(): NavigationItem {
    return null;
  }

  matchPage(page: string): boolean {
    return page &&  page.startsWith(this.route);
  }

}

export class NavigationProject implements  NavigationItem {

  constructor(
    public id: string,
    public label: string,
    public branches: Array<NavigationBranch>,
  ) {
  }



  get displayName(): string {
    return this.label;
  }

  get itemType(): string {
    return 'project';
  }

  get route(): string {
    return this.path();
  }

  selected = false;
  indeterminate = false;
  selectedBranch: NavigationBranch;
  stableBranch: NavigationBranch;
  relatedHierarchyNode: NavigationHierarchyNode;
  itemOptions = true ;
  itemOptionPlaceHolder: 'Stable branch by default';
  toBeDisplayed = false;

  static newFromApi(projectApi: ProjectApi): NavigationProject {

    const branches = Array<NavigationBranch>();
    const mapBranchNameBranch = new Map();
    const instance = new NavigationProject(
      projectApi.id,
      projectApi.label,
      branches
    );
    if (projectApi.branches !== null) {
      for (const currentBranchApi of projectApi.branches) {
        const currentBranch = new NavigationBranch(currentBranchApi.name, currentBranchApi.features, instance);
        branches.push(currentBranch);
        mapBranchNameBranch.set(currentBranch.name, currentBranch);
      }
    }
    instance.stableBranch = mapBranchNameBranch.get(projectApi.stableBranch);
    instance.stableBranch.project = instance;
    instance.selectedBranch = instance.stableBranch;
    return instance;
  }


  itemOptionSelected(): NavigationItem {
    return this.stableBranch ;
  }

  path() {
    return this.relatedHierarchyNode.path + '>' + this.id ;
  }

  itemChildren(): Array<NavigationItem> {
    return this.branches;
  }

  matchPage(page: string): boolean {
    return page &&  page.startsWith(this.route);
  }
}

export class NavigationHierarchyNode implements  NavigationItem {
  selected = false;
  indeterminate = false;
  open = false;
  level: number;
  path: string;
  children = Array<NavigationHierarchyNode>();
  projects = Array<NavigationProject>();
  root: NavigationHierarchyNode;
  itemOptions = false ;
  toBeDisplayed = false;

  constructor(
    public id: string,
    public slugName: string,
    public name: string,
    public childrenLabel: string,
    public childLabel: string) {
    this.level = this.id.split('.').length - 1;
  }

  static newFromApi(nodeApi: HierarchyNodeApi): NavigationHierarchyNode {
    return new NavigationHierarchyNode(
      nodeApi.id,
      nodeApi.slugName,
      nodeApi.name,
      nodeApi.childrenLabel,
      nodeApi.childLabel,
    );
  }


  hasChilden(): boolean {
    return this.children.length > 0;
  }

  hasProjects(): boolean {
    return this.projects.length > 0;
  }

  clone(): NavigationHierarchyNode {
    const instance = new NavigationHierarchyNode(
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



  get displayName(): string {
    return this.name;
  }

  get itemType(): string {
    return 'node';
  }

  itemChildren(): Array<NavigationItem> {
    const children = Array<NavigationItem>();
    this.children.forEach(d => children.push(d));
    this.projects.forEach(d => children.push(d));
    return children;
  }

  get route(): string {
    return this.path;
  }

  itemOptionSelected(): NavigationItem {
    return null;
  }

  matchPage(page: string): boolean {
    return page &&  page.startsWith(this.route);
  }
}
