import {HierarchyNodeApi, ProjectApi} from './hierarchy';

export interface NavigationWithOptions {
  itemOptions: boolean;
  itemOptionPlaceHolder?: string;
  itemOptionSelected() : NavigationItem;

}

export interface NavigationItem extends  NavigationWithOptions{
  selected: boolean;
  itemType: string;
  displayName: string;
  route?: string;
  toBeDisplayed: boolean;
  matchPage(page:string) : boolean;
  itemChildren(): NavigationItem[];
}

export class NavigationFeature implements NavigationItem{
  public selected: boolean = false ;
  public itemOptions: boolean= false ;
  public toBeDisplayed: boolean = true;
  constructor(
    public level: number,
    public value: string,
    public display: string,
    public directory: NavigationDirectory
  ) {

  }

  public matchPage(page:string) : boolean{
    return page &&  page.startsWith(this.route);
  }

  public path(){
    return this.directory.path() + "/" + this.value ;
  }

  public get displayName(): string {
    return this.display;
  }

  public get itemType(): string {
    return "feature";
  }

  public get route(): string {
    return this.path();
  }

  public  itemChildren(): NavigationItem[] {
    return Array<NavigationItem>();
  }

  public itemOptionSelected() : NavigationItem{
    return null;
  }

}

export class NavigationDirectory implements NavigationItem{

  public features: Array<NavigationFeature>;
  public directories: Array<NavigationDirectory>;
  public selected: boolean = false ;
  public itemOptions: boolean= false ;
  public toBeDisplayed: boolean = false;

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
    for (let k = 0; k < featureRowPath.length; k++) {
      const currentPath = featureRowPath[k];
      const split = currentPath.split('/', 2);
      if (split.length === 1) {
        this.features.push(new NavigationFeature(split.length, currentPath, currentPath.replace(".feature",""), this ));
      } else {
        if (split.length === 2) {
          const currentDirectory = split[0];
          if (currentDirectory !== lastDirectory ) {
            if ( lastDirectory != '') {
                this.directories.push(new NavigationDirectory(lastDirectory,  lastDirectoryFeatureRowPath, pathFromRoot +'/' +lastDirectory, branch));
            }
            lastDirectory = currentDirectory;
            lastDirectoryFeatureRowPath = new Array<string>();
          }
          lastDirectoryFeatureRowPath.push( split[1]);
        }
      }
    }
    if ( lastDirectory != '') {
       this.directories.push(new NavigationDirectory(lastDirectory,  lastDirectoryFeatureRowPath,pathFromRoot +'/' +lastDirectory,branch));
    }



  }

  public matchPage(page:string) : boolean{
    return page &&  page.startsWith(this.route);
  }

  public path(){
    return this.branch.path() + ">" + this.pathFromRoot ;
  }


  public get displayName(): string {
    return this.name;
  }

  public get itemType(): string {
    return "directory";
  }

  public  itemChildren(): NavigationItem[] {
    let children = Array<NavigationItem>();
    this.directories.forEach(d=> children.push(d));
    this.features.forEach(d=> children.push(d));
    return children;
  }

  public get route(): string {
    return this.path();
  }

  public itemOptionSelected() : NavigationItem{
    return null;
  }
}

export class NavigationBranch implements NavigationItem{

  public selected: boolean = false ;
  public featureFilter: NavigationFeature;
  public features: Array<NavigationFeature>;
  public rootDirectory: NavigationDirectory;
  public itemOptions: boolean= false ;
  public toBeDisplayed: boolean = false;
  constructor(
    public name: string,
    public featureRowPath: Array<string>,
    public project: NavigationProject
  ) {
    this.rootDirectory  = new NavigationDirectory(name, featureRowPath,"", this);
    this.features = new Array<NavigationFeature>();
    let lastDirectory = '';
    for (let k = 0; k < featureRowPath.length; k++) {
      const currentPath = featureRowPath[k];
      const split = currentPath.split('/');
      if (split.length === 1) {
        this.features.push(new NavigationFeature(split.length, currentPath, currentPath,this.rootDirectory));
      } else {
        if (split.length === 2) {
          const currentDirectory = split[0];
          if (currentDirectory !== lastDirectory) {
            this.features.push(new NavigationFeature(split.length - 1, currentDirectory, currentDirectory + ' :',this.rootDirectory));
            lastDirectory = currentDirectory;
          }
          this.features.push(new NavigationFeature(split.length, currentPath, split[1],this.rootDirectory));
        } else {
          this.features.push(new NavigationFeature(split.length, currentPath, currentPath,this.rootDirectory));
        }
      }
    }
  }

  public path(){
    return this.project.path() + ">" + this.name ;
  }

  public get displayName(): string {
    return this.name;
  }

  public get itemType(): string {
    return "directory";
  }

  public  itemChildren(): NavigationItem[] {
    return this.rootDirectory.itemChildren();
  }

  public get route(): string {
    return this.path();
  }

  public itemOptionSelected() : NavigationItem{
    return null;
  }

  public matchPage(page:string) : boolean{
    return page &&  page.startsWith(this.route);
  }

}

export class NavigationProject implements  NavigationItem{

  public selected = false;
  public indeterminate = false;
  public selectedBranch: NavigationBranch;
  public stableBranch: NavigationBranch;
  public relatedHierarchyNode: NavigationHierarchyNode;
  public itemOptions: boolean= true ;
  public itemOptionPlaceHolder : "Stable branch by default";
  public toBeDisplayed: boolean = false;

  constructor(
    public id: string,
    public label: string,
    public branches: Array<NavigationBranch>,
  ) {
  }


  public itemOptionSelected() : NavigationItem {
    return this.stableBranch ;
  }

  public path(){
    return this.relatedHierarchyNode.path + ">" + this.id ;
  }

  public static newFromApi(projectApi: ProjectApi): NavigationProject {

    const branches = Array<NavigationBranch>();
    const mapBranchNameBranch = new Map();
    const instance = new NavigationProject(
      projectApi.id,
      projectApi.label,
      branches
    );
    if (projectApi.branches !== null) {
      for (let k = 0; k < projectApi.branches.length; k++) {
        const currentBranch = new NavigationBranch(projectApi.branches[k].name, projectApi.branches[k].features, instance);
        branches.push(currentBranch);
        mapBranchNameBranch.set(currentBranch.name, currentBranch);
      }
    }
    instance.stableBranch = mapBranchNameBranch.get(projectApi.stableBranch);
    instance.stableBranch.project = instance;
    instance.selectedBranch = instance.stableBranch;
    return instance;
  }



  public get displayName(): string {
    return this.label;
  }

  public get itemType(): string {
    return "project";
  }

  public  itemChildren(): NavigationItem[] {
    return this.branches;
  }

  public get route(): string {
    return this.path();
  }

  public matchPage(page:string) : boolean{
    return page &&  page.startsWith(this.route);
  }
}

export class NavigationHierarchyNode implements  NavigationItem{
  public selected = false;
  public indeterminate = false;
  public open = false;
  public level: number;
  public path: string;
  public children = Array<NavigationHierarchyNode>();
  public projects = Array<NavigationProject>();
  public root: NavigationHierarchyNode;
  public itemOptions: boolean= false ;
  public toBeDisplayed: boolean = false;

  constructor(
    public id: string,
    public slugName: string,
    public name: string,
    public childrenLabel: string,
    public childLabel: string) {
    this.level = this.id.split('.').length - 1;
  }

  public static newFromApi(nodeApi: HierarchyNodeApi): NavigationHierarchyNode {
    return new NavigationHierarchyNode(
      nodeApi.id,
      nodeApi.slugName,
      nodeApi.name,
      nodeApi.childrenLabel,
      nodeApi.childLabel,
    );
  }


  public hasChilden(): boolean {
    return this.children.length > 0;
  }

  public hasProjects(): boolean {
    return this.projects.length > 0;
  }

  public clone(): NavigationHierarchyNode {
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



  public get displayName(): string {
    return this.name;
  }

  public get itemType(): string {
    return "node";
  }

  public itemChildren(): NavigationItem[] {
    let children = Array<NavigationItem>();
    this.children.forEach(d=> children.push(d));
    this.projects.forEach(d=> children.push(d));
    return children;
  }

  public get route(): string {
    return this.path;
  }

  public itemOptionSelected() : NavigationItem{
    return null;
  }

  public matchPage(page:string) : boolean{
    return page &&  page.startsWith(this.route);
  }
}
