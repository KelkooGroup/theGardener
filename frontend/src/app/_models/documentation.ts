export class DocumentationStepApi {
  public id: string;
  public keyword: string;
  public text: string;
  public argument: Array<Array<string>>;
}

export class DocumentationExamplesApi {
  public id: string;
  public keyword: string;
  public description: string;
  public tableBody: Array<Array<string>>;
  public tableHeader: Array<string>;
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
  public examples: Array<DocumentationExamplesApi>;
}

export class DocumentationFeatureApi {
  public id: string;
  public path: string;
  public name: string;
  public description: string;
  public tags: Array<string>;
  public comments: Array<string>;
  public keyword: string;
  public background: DocumentationScenarioApi;
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

export interface ExpandableNode {

  type: string;
  nodeId: string;
  localId: string;

  getChilden(): Array<ExpandableNode>;

  hasChilden(): boolean;
}

export class DocumentationNode implements ExpandableNode {
  public type = 'node';
  public nodeId: string;
  public localId: string;
  public data: DocumentationNodeApi;
  public level: number;
  public children: Array<DocumentationNode>;
  public projects: Array<DocumentationProject>;

  public static toAnchor(id: string): string {
    return id.split(' ').join('_').split('/').join('-').split('.').join('-').split('#').join('-').toLocaleLowerCase();
  }

  public static newFromApi(parentNodeId: string, dataApi: DocumentationNodeApi, level: number): DocumentationNode {
    const instance = new DocumentationNode();
    instance.data = dataApi;
    instance.localId = DocumentationNode.toAnchor(dataApi.slugName);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    instance.children = [];
    for (const child of dataApi.children) {
      instance.children.push(DocumentationNode.newFromApi(instance.nodeId, child, level + 1));
    }
    instance.projects = [];
    for (const project of dataApi.projects) {
      instance.projects.push(DocumentationProject.newFromApi(instance.nodeId, project, level + 1));
    }
    return instance;
  }

  getChilden(): Array<ExpandableNode> {
    const all = [];
    for (const child of this.children) {
      all.push(child);
    }
    for (const project of this.projects) {
      all.push(project);
    }
    return all;
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0;
  }


}

export class DocumentationStepRow {
  public values: { [key: string]: string; } = {};

  public getValue(key: string): string {
    return this.values[key];
  }
}

export class DocumentationStepTable {
  public headers: { [key: string]: string; } = {};
  public headerIds = [];
  public rows = [];

  public getHeader(headerId: string): string {
    return this.headers[headerId];
  }
}


export class DocumentationStepTextFragment {
  constructor(public text: string, public isParameter: boolean) {
  }
}

export class DocumentationStep implements ExpandableNode {
  public type: string;
  public nodeId: string;
  public localId: string;
  public data: DocumentationStepApi;
  public text = [];
  public hasTable: boolean;
  public table: DocumentationStepTable;
  public hasLongText: boolean;
  public longText: string;


  public static newFromApi(dataApi: DocumentationStepApi): DocumentationStep {
    const instance = new DocumentationStep();
    instance.data = dataApi;
    instance.hasTable = false ;
    instance.hasLongText = false ;

    if (dataApi.argument != null && dataApi.argument.length > 0){

      if (dataApi.argument.length > 1) {
        instance.hasTable = true;
      } else {
        instance.hasLongText = dataApi.argument[0].length == 1;
        instance.longText = dataApi.argument[0][0];
      }

    }

    if (instance.hasTable) {
      instance.table = new DocumentationStepTable();
      for (let j = 0; j < dataApi.argument.length; j++) {
        const currentRowValues = dataApi.argument[j];
        if (j === 0) {
          for (let l = 0; l < currentRowValues.length; l++) {
            instance.table.headerIds.push(l + '');
            instance.table.headers[l] = currentRowValues[l];
          }
        } else {
          const row = new DocumentationStepRow();
          for (let k = 0; k < currentRowValues.length; k++) {
            row.values[instance.table.headerIds[k]] = currentRowValues[k];
          }
          instance.table.rows.push(row);
        }
      }
    }
    const fragments = instance.data.text.split('"');
    if (fragments.length > 0) {
      if (fragments.length === 1) {
        instance.text.push(new DocumentationStepTextFragment(instance.data.text, false));
      } else {
        let isParameter = false;
        for (let j = 0; j < fragments.length; j++) {
          instance.text.push(new DocumentationStepTextFragment(fragments[j], isParameter));
          isParameter = !isParameter;
        }
      }
    }
    return instance;
  }

  getChilden(): Array<ExpandableNode> {
    return [];
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0;
  }
}

export class DocumentationExamples {

  public data: DocumentationExamplesApi;
  public table: DocumentationStepTable;

  public static newFromApi(dataApi: DocumentationExamplesApi): DocumentationExamples {
    const instance = new DocumentationExamples();
    instance.data = dataApi;
    instance.table = new DocumentationStepTable();

    for (let j = 0; j < dataApi.tableHeader.length; j++) {
      instance.table.headerIds.push(j + '');
      instance.table.headers[j] = dataApi.tableHeader[j];
    }
    for (let k = 0; k < dataApi.tableBody.length; k++) {
      const row = new DocumentationStepRow();
      for (let l = 0; l < dataApi.tableBody[k].length; l++) {
        row.values[instance.table.headerIds[l]] = dataApi.tableBody[k][l];
      }
      instance.table.rows.push(row);
    }

    return instance;
  }

}

export class DocumentationScenario implements ExpandableNode {
  public type = 'scenario';
  public nodeId: string;
  public localId: string;
  public data: DocumentationScenarioApi;
  public level: number;
  public steps: Array<DocumentationStep>;
  public examples: DocumentationExamples;

  public static newFromApi(type: string, parentNodeId: string, dataApi: DocumentationScenarioApi, level: number): DocumentationScenario {
    const instance = new DocumentationScenario();
    instance.type = type;
    instance.data = dataApi;
    instance.localId = DocumentationNode.toAnchor(dataApi.name);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    instance.steps = [];
    for (const step of dataApi.steps) {
      instance.steps.push(DocumentationStep.newFromApi(step));
    }
    if (dataApi.examples && dataApi.examples.length > 0) {
      instance.examples = DocumentationExamples.newFromApi(dataApi.examples[0]);
    }

    return instance;
  }

  getChilden(): Array<ExpandableNode> {
    if (this.type !== 'background') {
      return [];
    } else {
      return this.steps;
    }
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0;
  }
}


export class DocumentationFeature implements ExpandableNode {
  public type = 'feature';
  public nodeId: string;
  public localId: string;
  public data: DocumentationFeatureApi;
  public level: number;
  public background: DocumentationScenario;
  public scenarios: Array<DocumentationScenario>;
  public children: Array<DocumentationScenario>;

  public static newFromApi(parentNodeId: string, dataApi: DocumentationFeatureApi, level: number): DocumentationFeature {
    const instance = new DocumentationFeature();
    instance.data = dataApi;
    instance.localId = DocumentationNode.toAnchor(dataApi.path);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    if (dataApi.background) {
      instance.background = DocumentationScenario.newFromApi('background', instance.nodeId + '_background', dataApi.background, level + 1);
    }

    instance.scenarios = [];
    for (const scenario of dataApi.scenarios) {
      instance.scenarios.push(DocumentationScenario.newFromApi('scenario', instance.nodeId, scenario, level + 1));
    }

    instance.children = [];
    if (instance.background) {
      instance.children.push(instance.background);
    }
    for (const scenario of instance.scenarios) {
      instance.children.push(scenario);
    }

    return instance;
  }

  getChilden(): Array<ExpandableNode> {
    return this.children;
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0;
  }
}

export class DocumentationBranch {
  public data: DocumentationBranchApi;
  public nodeId: string;
  public localId: string;
  public level: number;
  public features: Array<DocumentationFeature>;

  public static newFromApi(parentNodeId: string, dataApi: DocumentationBranchApi, level: number): DocumentationBranch {
    const instance = new DocumentationBranch();
    instance.data = dataApi;
    instance.localId = DocumentationNode.toAnchor(dataApi.name);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    instance.features = [];
    for (const feature of dataApi.features) {
      instance.features.push(DocumentationFeature.newFromApi(instance.nodeId, feature, level + 1));
    }
    return instance;
  }
}


export class DocumentationProject implements ExpandableNode {
  public type = 'project';
  public nodeId: string;
  public localId: string;
  public data: DocumentationProjectApi;
  public level: number;
  public branch: DocumentationBranch;

  public static newFromApi(parentNodeId: string, dataApi: DocumentationProjectApi, level: number): DocumentationProject {
    const instance = new DocumentationProject();
    instance.data = dataApi;
    instance.localId = DocumentationNode.toAnchor(dataApi.name);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    for (const branch of dataApi.branches) {
      instance.branch = DocumentationBranch.newFromApi(instance.nodeId, branch, level);
    }
    return instance;
  }

  getChilden(): Array<ExpandableNode> {
    if (this.branch) {
      return this.branch.features;
    } else {
      return [];
    }
  }

  hasChilden(): boolean {
    return this.getChilden().length > 0;
  }
}



