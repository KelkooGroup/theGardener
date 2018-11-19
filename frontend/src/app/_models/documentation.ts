
export class DocumentationStepApi {
  public id: string;
  public keyword: string;
  public text: string;
  public argument: Array<Array<string>>;
}

export class DocumentationScenarioApi {
  public id: string;
  public name: string;
  public abstractionLevel: string;
  public caseType: string;
  public workflowStep: string;
  public keyword: string;
  public description: string;
  public tags: Array<string>;
  public steps: Array<DocumentationStepApi>;
}

export class DocumentationFeatureApi {
  public id: string;
  public path: string;
  public name: string;
  public description: string;
  public tags: Array<string>;
  public comments: Array<string>;
  public keyword: string;
  public background : DocumentationScenarioApi ;
  public scenarios: Array<DocumentationScenarioApi>;
}

export class DocumentationBranchApi {
  public id: string;
  public name: string;
  public isStable: string;
  public features: Array<DocumentationFeatureApi>;
}

export class DocumentationProjectApi {
  public id: string;
  public name: string;
  public stableBranch: string;
  public branches: Array<DocumentationBranchApi>;
}

export class DocumentationNodeApi {
  public id: string;
  public slugName: string;
  public name: string;
  public childrenLabel: string;
  public childLabel: string;
  public children: Array<DocumentationNodeApi>;
  public projects: Array<DocumentationProjectApi>;
}


export class DocumentationStepRow{
  public values : { [key:string]:string; } = {};

  public getValue(key: string):string {
    return this.values[key] ;
  }
}

export class DocumentationStepTable{
  public headers = new  Array<string>();
  public rows    = new Array<DocumentationStepRow>();
}


export class DocumentationStepTextFragment{
  constructor(public text: string, public isParameter: boolean){}
}

export class DocumentationStep  implements ExpandableNode {
  public type : string ;
  public nodeId : string ;
  public localId : string ;
  public data: DocumentationStepApi;
  public text = new  Array<DocumentationStepTextFragment>();
  public hasTable: boolean;
  public table: DocumentationStepTable;


  public static newFromApi(dataApi: DocumentationStepApi): DocumentationStep {
    var instance = new DocumentationStep( );
    instance.data = dataApi ;
    instance.hasTable = dataApi.argument != null && dataApi.argument.length > 0;
    if (instance.hasTable){
      instance.table = new DocumentationStepTable();
      for (var j = 0; j < dataApi.argument.length; j++) {
        var currentRowValues = dataApi.argument[j];
        if (j ==0) {
          instance.table.headers = currentRowValues;
        }else{
          var row = new DocumentationStepRow() ;
          for (var k = 0; k < currentRowValues.length; k++) {
            row.values[instance.table.headers[k]] = currentRowValues[k] ;
          }
          instance.table.rows.push(row);
        }
      }
    }
    var fragments = instance.data.text.split('"') ;
    if (fragments.length > 0) {
      if (fragments.length == 1) {
        instance.text.push( new DocumentationStepTextFragment(instance.data.text, false) ) ;
      } else {
        var isParameter = false ;
        for (var j = 0; j < fragments.length; j++) {
          instance.text.push( new DocumentationStepTextFragment(fragments[j], isParameter) )
          isParameter = ! isParameter ;
        }
      }
    }
    return instance;
  }

  getChilden() : Array<ExpandableNode>{
      return new Array<ExpandableNode>();
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0 ;
  }
}

export interface ExpandableNode {

  type : string ;
  nodeId : string ;
  localId : string ;

  getChilden() : Array<ExpandableNode> ;

  hasChilden(): boolean ;

}


export class DocumentationScenario  implements ExpandableNode {
  public type = "scenario" ;
  public nodeId : string ;
  public localId : string ;
  public data: DocumentationScenarioApi;
  public level : number;
  public steps: Array<DocumentationStep>;

  public static newFromApi(type: string,   parentNodeId: string,dataApi: DocumentationScenarioApi, level: number): DocumentationScenario {
    var instance = new DocumentationScenario( );
    instance.type = type ;
    instance.data = dataApi ;
    instance.localId =  DocumentationNode.toAnchor( dataApi.name) ;
    instance.nodeId  = `${parentNodeId}_${instance.localId}` ;
    instance.level = level ;
    instance.steps = new Array<DocumentationStep>() ;
    for (let step of dataApi.steps) {
      instance.steps.push( DocumentationStep.newFromApi(step)   );
    }
    return instance;
  }

  getChilden() : Array<ExpandableNode>{
    if (  this.type == "scenario" ) {
      return new Array<ExpandableNode>();
    }else{
      return this.steps ;
    }
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0 ;
  }
}


export class DocumentationFeature implements ExpandableNode{
  public type = "feature" ;
  public nodeId : string ;
  public localId : string ;
  public data: DocumentationFeatureApi;
  public level : number;
  public background : DocumentationScenario ;
  public scenarios: Array<DocumentationScenario>;
  public children:  Array<DocumentationScenario>;

  public static newFromApi(parentNodeId: string, dataApi: DocumentationFeatureApi, level: number): DocumentationFeature {
    var instance = new DocumentationFeature( );
    instance.data = dataApi ;
    instance.localId = DocumentationNode.toAnchor( dataApi.path);
    instance.nodeId  = `${parentNodeId}_${instance.localId}` ;
    instance.level = level ;
    if( dataApi.background ){
      instance.background   = DocumentationScenario.newFromApi("background", instance.nodeId+"_background",dataApi.background, level+1)
    }

    instance.scenarios = new Array<DocumentationScenario>() ;
    for (let scenario of dataApi.scenarios) {
      instance.scenarios.push( DocumentationScenario.newFromApi("scenario", instance.nodeId,scenario, level+1)   );
    }

    instance.children = new Array<DocumentationScenario>() ;
    if ( instance.background  ){
      instance.children.push(instance.background)
    }
    for (let scenario of instance.scenarios) {
      instance.children.push( scenario   );
    }

    return instance;
  }

  getChilden() : Array<ExpandableNode>{
    return this.children;
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0 ;
  }
}

export class DocumentationBranch {
  public data: DocumentationBranchApi;
  public nodeId : string ;
  public localId : string ;
  public level : number;
  public features: Array<DocumentationFeature>;

  public static newFromApi(parentNodeId: string, dataApi: DocumentationBranchApi, level: number): DocumentationBranch {
    var instance = new DocumentationBranch( );
    instance.data = dataApi ;
    instance.localId = DocumentationNode.toAnchor( dataApi.name) ;
    instance.nodeId  = `${parentNodeId}_${instance.localId}` ;
    instance.level = level ;
    instance.features = new Array<DocumentationFeature>() ;
    for (let feature of dataApi.features) {
      instance.features.push( DocumentationFeature.newFromApi(instance.nodeId,feature,level+1));
    }
    return instance;
  }
}


export class DocumentationProject implements ExpandableNode{
  public type = "project" ;
  public nodeId : string ;
  public localId : string ;
  public data: DocumentationProjectApi;
  public level : number;
  public branch: DocumentationBranch;

  public static newFromApi(parentNodeId: string, dataApi: DocumentationProjectApi, level: number): DocumentationProject  {
    var instance = new DocumentationProject( );
    instance.data = dataApi ;
    instance.localId = DocumentationNode.toAnchor( dataApi.name)  ;
    instance.nodeId  = `${parentNodeId}_${instance.localId}` ;
    instance.level = level ;
    for (let branch of dataApi.branches) {
      instance.branch =  DocumentationBranch.newFromApi(instance.nodeId,branch,level)   ;
    }
    return instance;
  }

  getChilden() : Array<ExpandableNode>{
    return this.branch.features ;
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0 ;
  }
}

export class DocumentationNode implements ExpandableNode{
  public type = "node" ;
  public nodeId : string ;
  public localId : string ;
  public data: DocumentationNodeApi;
  public level : number;
  public children: Array<DocumentationNode>;
  public projects: Array<DocumentationProject>;

  public static toAnchor( id: string) : string{
    return id.split(' ').join('_').split('/').join('-').split('.').join('-').split('#').join('-').toLocaleLowerCase()
  }

  public static newFromApi(parentNodeId: string, dataApi: DocumentationNodeApi, level: number): DocumentationNode  {
    var instance = new DocumentationNode( );
    instance.data = dataApi ;
    instance.localId = DocumentationNode.toAnchor( dataApi.slugName) ;
    instance.nodeId  = `${parentNodeId}_${instance.localId}` ;
    instance.level = level ;
    instance.children = new Array<DocumentationNode>() ;
    for (let child of dataApi.children) {
      instance.children.push( DocumentationNode.newFromApi(instance.nodeId , child,level+1) )  ;
    }
    instance.projects = new Array<DocumentationProject>() ;
    for (let project of dataApi.projects) {
      instance.projects.push( DocumentationProject.newFromApi(instance.nodeId ,project,level+1) )  ;
    }
    return instance;
  }

  getChilden() : Array<ExpandableNode>{
    var all = new Array<ExpandableNode>();
    for (let child of this.children) {
      all.push( child )  ;
    }
    for (let project of this.projects) {
      all.push( project )  ;
    }
    return all ;
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0 ;
  }


}



