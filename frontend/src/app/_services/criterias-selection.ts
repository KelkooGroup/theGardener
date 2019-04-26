import {HierarchyNodeApi, ProjectApi} from '../_models/criterias';

export interface NavigationItem {
  selected: boolean;
  itemType: string;
  displayName: string;
  route?: string;
  toBeDisplayed: boolean;
  matchPage(page:string) : boolean;
  itemOptions: boolean;
  itemOptionPlaceHolder?: string;
  itemOptionSelected() : NavigationItem;
  itemChildren(): NavigationItem[];
}

export class FeatureSelector implements NavigationItem{
  public selected: boolean = false ;
  public itemOptions: boolean= false ;
  public toBeDisplayed: boolean = true;
  constructor(
    public level: number,
    public value: string,
    public display: string,
    public directory: DirectorySelector
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

export class DirectorySelector implements NavigationItem{

  public features: Array<FeatureSelector>;
  public directories: Array<DirectorySelector>;
  public selected: boolean = false ;
  public itemOptions: boolean= false ;
  public toBeDisplayed: boolean = false;

  constructor(
    public name: string,
    featureRowPath: Array<string>,
    public pathFromRoot: string,
    public branch: BranchSelector
  ) {
    this.features = new Array<FeatureSelector>();
    this.directories = new Array<DirectorySelector>();
    let lastDirectory = '';
    let lastDirectoryFeatureRowPath = new Array<string>();
    for (let k = 0; k < featureRowPath.length; k++) {
      const currentPath = featureRowPath[k];
      const split = currentPath.split('/', 2);
      if (split.length === 1) {
        this.features.push(new FeatureSelector(split.length, currentPath, currentPath.replace(".feature",""), this ));
      } else {
        if (split.length === 2) {
          const currentDirectory = split[0];
          if (currentDirectory !== lastDirectory ) {
            if ( lastDirectory != '') {
                this.directories.push(new DirectorySelector(lastDirectory,  lastDirectoryFeatureRowPath, pathFromRoot +'/' +lastDirectory, branch));
            }
            lastDirectory = currentDirectory;
            lastDirectoryFeatureRowPath = new Array<string>();
          }
          lastDirectoryFeatureRowPath.push( split[1]);
        }
      }
    }
    if ( lastDirectory != '') {
       this.directories.push(new DirectorySelector(lastDirectory,  lastDirectoryFeatureRowPath,pathFromRoot +'/' +lastDirectory,branch));
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

export class BranchSelector implements NavigationItem{

  public selected: boolean = false ;
  public featureFilter: FeatureSelector;
  public features: Array<FeatureSelector>;
  public rootDirectory: DirectorySelector;
  public itemOptions: boolean= false ;
  public toBeDisplayed: boolean = false;
  constructor(
    public name: string,
    public featureRowPath: Array<string>,
    public project: ProjectSelector
  ) {
    this.rootDirectory  = new DirectorySelector(name, featureRowPath,"", this);
    this.features = new Array<FeatureSelector>();
    let lastDirectory = '';
    for (let k = 0; k < featureRowPath.length; k++) {
      const currentPath = featureRowPath[k];
      const split = currentPath.split('/');
      if (split.length === 1) {
        this.features.push(new FeatureSelector(split.length, currentPath, currentPath,this.rootDirectory));
      } else {
        if (split.length === 2) {
          const currentDirectory = split[0];
          if (currentDirectory !== lastDirectory) {
            this.features.push(new FeatureSelector(split.length - 1, currentDirectory, currentDirectory + ' :',this.rootDirectory));
            lastDirectory = currentDirectory;
          }
          this.features.push(new FeatureSelector(split.length, currentPath, split[1],this.rootDirectory));
        } else {
          this.features.push(new FeatureSelector(split.length, currentPath, currentPath,this.rootDirectory));
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

export class ProjectSelector implements  NavigationItem{

  public selected = false;
  public indeterminate = false;
  public selectedBranch: BranchSelector;
  public stableBranch: BranchSelector;
  public relatedHierarchyNode: HierarchyNodeSelector;
  public itemOptions: boolean= true ;
  public itemOptionPlaceHolder : "Stable branch by default";
  public toBeDisplayed: boolean = false;

  constructor(
    public id: string,
    public label: string,
    public branches: Array<BranchSelector>,
  ) {
  }


  public itemOptionSelected() : NavigationItem {
    return this.stableBranch ;
  }

  public path(){
    return this.relatedHierarchyNode.path + ">" + this.id ;
  }

  public static newFromApi(projectApi: ProjectApi): ProjectSelector {

    const branches = Array<BranchSelector>();
    const mapBranchNameBranch = new Map();
    const instance = new ProjectSelector(
      projectApi.id,
      projectApi.label,
      branches
    );
    if (projectApi.branches !== null) {



      for (let k = 0; k < projectApi.branches.length; k++) {

       if (projectApi.id == "publisherManagementBO" && projectApi.branches[k].name == "qa"){
         projectApi.id;
       }


        const currentBranch = new BranchSelector(projectApi.branches[k].name, projectApi.branches[k].features, instance);
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

export class HierarchyNodeSelector implements  NavigationItem{
  public selected = false;
  public indeterminate = false;
  public open = false;
  public level: number;
  public path: string;
  public children = Array<HierarchyNodeSelector>();
  public projects = Array<ProjectSelector>();
  public root: HierarchyNodeSelector;
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

  public static newFromApi(nodeApi: HierarchyNodeApi): HierarchyNodeSelector {
    return new HierarchyNodeSelector(
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
