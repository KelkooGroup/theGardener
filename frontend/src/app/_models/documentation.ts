
export class DocumentationStepApi {
  public id: string;
  public keyword: string;
  public text: string;
  public argument: Array<string>;
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

export class DocumentationStep {
  public data: DocumentationStepApi;

  public static newFromApi(dataApi: DocumentationStepApi): DocumentationStep {
    var instance = new DocumentationStep( );
    instance.data = dataApi ;
    return instance;
  }
}

export class DocumentationScenario {
  public data: DocumentationScenarioApi;
  public level : number;
  public steps: Array<DocumentationStep>;

  public static newFromApi(dataApi: DocumentationScenarioApi, level: number): DocumentationScenario {
    var instance = new DocumentationScenario( );
    instance.data = dataApi ;
    instance.level = level ;
    instance.steps = new Array<DocumentationStep>() ;
    for (let step of dataApi.steps) {
      instance.steps.push( DocumentationStep.newFromApi(step)   );
    }
    return instance;
  }
}

export class DocumentationFeature {
  public data: DocumentationFeatureApi;
  public level : number;
  public scenarios: Array<DocumentationScenario>;

  public static newFromApi(dataApi: DocumentationFeatureApi, level: number): DocumentationFeature {
    var instance = new DocumentationFeature( );
    instance.data = dataApi ;
    instance.level = level ;
    instance.scenarios = new Array<DocumentationScenario>() ;
    for (let scenario of dataApi.scenarios) {
      instance.scenarios.push( DocumentationScenario.newFromApi(scenario, level+1)   );
    }
    return instance;
  }
}

export class DocumentationBranch {
  public data: DocumentationBranchApi;
  public level : number;
  public features: Array<DocumentationFeature>;

  public static newFromApi(dataApi: DocumentationBranchApi, level: number): DocumentationBranch {
    var instance = new DocumentationBranch( );
    instance.data = dataApi ;
    instance.level = level ;
    instance.features = new Array<DocumentationFeature>() ;
    for (let feature of dataApi.features) {
      instance.features.push( DocumentationFeature.newFromApi(feature,level+1));
    }
    return instance;
  }
}

export class DocumentationProject {
  public data: DocumentationProjectApi;
  public level : number;
  public branch: DocumentationBranch;

  public static newFromApi(dataApi: DocumentationProjectApi, level: number): DocumentationProject  {
    var instance = new DocumentationProject( );
    instance.data = dataApi ;
    instance.level = level ;
    for (let branch of dataApi.branches) {
      instance.branch =  DocumentationBranch.newFromApi(branch,level)   ;
    }
    return instance;
  }
}

export class DocumentationNode {
  public data: DocumentationNodeApi;
  public level : number;
  public children: Array<DocumentationNode>;
  public projects: Array<DocumentationProject>;

  public static newFromApi(dataApi: DocumentationNodeApi, level: number): DocumentationNode  {
    var instance = new DocumentationNode( );
    instance.data = dataApi ;
    instance.level = level ;
    instance.children = new Array<DocumentationNode>() ;
    for (let child of dataApi.children) {
      instance.children.push( DocumentationNode.newFromApi(child,level+1) )  ;
    }
    instance.projects = new Array<DocumentationProject>() ;
    for (let project of dataApi.projects) {
      instance.projects.push( DocumentationProject.newFromApi(project,level+1) )  ;
    }
    return instance;
  }

  public hasChilden(): boolean {
    return this.children.length > 0;
  }

  public hasProjects(): boolean {
    return this.projects.length > 0;
  }
}
