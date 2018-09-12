

class TupleHierarchy{
  constructor(
    public taken : Array<HierarchyNodeApi>,
    public left : Array<HierarchyNodeApi>
  ) { }
}

export class SelectedProjectApi{
  constructor(
    public selected : boolean,
    public project : ProjectApi
  ) { }
}

export class BranchApi {
  constructor(
    public branch : string,
    public project : ProjectApi
) { }

}

export class ProjectApi{
  public id : string ;
  public label : string ;
  public stableBranch : string;
  public stableBranchAsObject : BranchApi;
  public relatedNode: HierarchyNodeApi;
  public selectedBranch : BranchApi;
  public branches : Array<string>;
  public branchesAsObjects : Array<BranchApi>;
}

export class HierarchyNodeApi {
  public id: string;
  public slugName: string;
  public name: string;
  public children: Array<HierarchyNodeApi>;
  public childrenLabel: string;
  public childLabel: string;
  public projects: Array<ProjectApi>;

  public level() : number {
    return this.id.split(".").length - 1 ;
  }

  public clone(): HierarchyNodeApi{
    var instance = new HierarchyNodeApi ;
    instance.id = this.id;
    instance.slugName = this.slugName;
    instance.name = this.name;
    instance.children = this.children;
    instance.childrenLabel = this.childrenLabel;
    instance.childLabel = this.childLabel;
    return instance ;
  }


  public static setCrossLinks(all: Array<HierarchyNodeApi>) {
    for (var i = 0; i < all.length; i++){
      var loopNode = all[i];
      if ( loopNode.projects != null ) {
        for (var j = 0; j < loopNode.projects.length; j++) {
          var loopProject = loopNode.projects[j];
          loopProject.relatedNode = loopNode;
          loopProject.stableBranchAsObject =   new BranchApi( loopProject.stableBranch, loopProject) ;
          loopProject.selectedBranch = loopProject.stableBranchAsObject;
          if (loopProject.branches != null){
            loopProject.branchesAsObjects = new Array<BranchApi>();
            for (var k = 0; k < loopProject.branches.length; k++) {
              loopProject.branchesAsObjects.push(  new BranchApi( loopProject.branches[k], loopProject)  ) ;
            }
          }
        }
      }
    }
  }


  public static buildTree( all: Array<HierarchyNodeApi>) : HierarchyNodeApi {
    var root : HierarchyNodeApi = null;
    if (all.length > 0 ){
      root = all[0];
      var tuple = HierarchyNodeApi.build(root, all) ;
      root.children = tuple.taken;
    }
    return root ;
  }



  private static build(node: HierarchyNodeApi, children: Array<HierarchyNodeApi>) : TupleHierarchy {

    var taken = new Array<HierarchyNodeApi>();
    var left  = new Array<HierarchyNodeApi>();

    if (children.length > 0) {
      for (var i = 0; i < children.length; i++)
      {
        var loopNode = children[i];
        if (  loopNode.id.startsWith(node.id) ){
          if ( loopNode.id.length  == node.id.length +3 ){
            taken.push(loopNode);
          }
          if ( loopNode.id.length  > node.id.length +3 ){
            left.push(loopNode);
          }
        }
      }
    }
    if ( taken.length > 0 ){
      for (var i = 0; i < taken.length; i++){
        var loopTaken = taken[i];
        var tuple = HierarchyNodeApi.build(loopTaken, Object.assign(  [], left )) ;
        loopTaken.children = tuple.taken ;
      }
    }


    return new TupleHierarchy(taken, left);

  }


}

export class CriteriasV1 {

  private allProjectsUnder  = new Array<HierarchyNodeApi> () ;
  private otherProjects  = new Array<SelectedProjectApi> () ;

  public includeHierarchyNode(node : HierarchyNodeApi){
    var temp = new Array<HierarchyNodeApi> () ;
    for (var i = 0; i < this.allProjectsUnder.length; i++){
      var currentNode = this.allProjectsUnder[i] ;
      if ( ! node.id.startsWith( currentNode.id )){
        temp.push(currentNode) ;
      }
    }
    temp.push(   node) ;
    this.allProjectsUnder = temp;
  }

  public excludeHierarchyNode(node : HierarchyNodeApi){
    var temp = new Array<HierarchyNodeApi> () ;
    for (var i = 0; i < this.allProjectsUnder.length; i++){
      var currentNode = this.allProjectsUnder[i] ;
      if (  node.id != currentNode.id ){
        temp.push(currentNode) ;
      }
    }
    this.allProjectsUnder = temp;
  }

  public includeProject(project : ProjectApi){
    var tempProjects = new Array<SelectedProjectApi> () ;
    for (var i = 0; i < this.otherProjects.length; i++) {
      var currentProject = this.otherProjects[i];
      if ( currentProject.project.id != project.id){
        tempProjects.push(currentProject) ;
      }
    }
    tempProjects.push( new SelectedProjectApi (true,project) );

    this.otherProjects = tempProjects;
  }

  public excludeProject(project : ProjectApi){
    var tempProjects = new Array<SelectedProjectApi> () ;
    for (var i = 0; i < this.otherProjects.length; i++) {
      var currentProject = this.otherProjects[i];
      if ( currentProject.project.id != project.id){
        tempProjects.push(currentProject) ;
      }
    }
    tempProjects.push( new SelectedProjectApi (false,project) );

    this.otherProjects = tempProjects;
  }


  public changeBranch(branch : BranchApi){

    for (var i = 0; i < this.otherProjects.length; i++) {
      var currentProject = this.otherProjects[i];
      if ( currentProject.project.id != branch.project.id){
        currentProject.project.selectedBranch = branch ;
      }
    }

  }

  public httpParams()  : string {
    var httpParams = "projectsInNodes=" ;
    for (var i = 0; i < this.allProjectsUnder.length; i++){
      var currentNode = this.allProjectsUnder[i] ;
      var param = currentNode.id ;
      if (i< this.allProjectsUnder.length-1){
        httpParams +=  `${param}|` ;
      }else{
        httpParams +=  `${param}` ;
      }
    }
    httpParams +=  `&projects=` ;
    for (var i = 0; i < this.otherProjects.length; i++){
      var currentProject = this.otherProjects[i] ;
      var param = `${currentProject.project.id}#${currentProject.project.selectedBranch.branch}@${currentProject.project.relatedNode.id}` ;
      var operator =  currentProject.selected ? "+" : "-" ;
      if (i< this.otherProjects.length-1){
        httpParams +=  `${operator}${param}|` ;
      }else{
        httpParams +=  `${operator}${param}` ;
      }
    }
    return httpParams ;
  }


}

