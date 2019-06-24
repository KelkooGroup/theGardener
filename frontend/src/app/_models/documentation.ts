export class DocumentationStepApi {
  id: string;
  keyword: string;
  text: string;
  argument: Array<Array<string>>;
}

export class DocumentationExamplesApi {
  id: string;
  keyword: string;
  description: string;
  tableBody: Array<Array<string>>;
  tableHeader: Array<string>;
}

export class DocumentationScenarioApi {
  id: string;
  name: string;
  abstractionLevel: string;
  caseType: string;
  workflowStep: string;
  keyword: string;
  description: string;
  tags: Array<string>;
  steps: Array<DocumentationStepApi>;
  examples?: Array<DocumentationExamplesApi>;
}

export class DocumentationFeatureApi {
  id: string;
  path: string;
  name: string;
  description: string;
  tags: Array<string>;
  comments: Array<string>;
  keyword: string;
  background?: DocumentationScenarioApi;
  scenarios: Array<DocumentationScenarioApi>;
}

export class DocumentationBranchApi {
  id: string;
  name: string;
  isStable: boolean;
  features: Array<DocumentationFeatureApi>;
}

export class DocumentationProjectApi {
  id: string;
  name: string;
  stableBranch?: string;
  branches: Array<DocumentationBranchApi>;
}

export class DocumentationNodeApi {
  id: string;
  slugName: string;
  name: string;
  childrenLabel: string;
  childLabel: string;
  children: Array<DocumentationNodeApi>;
  projects: Array<DocumentationProjectApi>;
}

export interface ExpandableNode {

  type: string;
  nodeId: string;
  localId: string;

  getChilden(): Array<ExpandableNode>;

  hasChilden(): boolean;
}

export class DocumentationNode implements ExpandableNode {
  type = 'node';
  nodeId: string;
  localId: string;
  data: DocumentationNodeApi;
  level: number;
  children: Array<DocumentationNode>;
  projects: Array<DocumentationProject>;

  static toAnchor(id: string): string {
    return id
      .split(' ')
      .join('_')
      .split('/')
      .join('-')
      .split('.')
      .join('-')
      .split('#')
      .join('-')
      .split(':')
      .join('-')
      .toLocaleLowerCase();
  }

  static newFromApi(parentNodeId: string, dataApi: DocumentationNodeApi, level: number): DocumentationNode {
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
  values: { [key: string]: string; } = {};

  getValue(key: string): string {
    return this.values[key];
  }
}

export class DocumentationStepTable {
  headers: { [key: string]: string; } = {};
  headerIds: Array<string> = [];
  rows: Array<DocumentationStepRow>  = [];

  getHeader(headerId: string): string {
    return this.headers[headerId];
  }
}


export class DocumentationStepTextFragment {
  constructor(public text: string, public isParameter: boolean) {
  }
}

export class DocumentationStep implements ExpandableNode {
  type: string;
  nodeId: string;
  localId: string;
  data: DocumentationStepApi;
  text: Array<DocumentationStepTextFragment>  = [];
  hasTable: boolean;
  table: DocumentationStepTable;
  hasLongText: boolean;
  longText: string;


  static newFromApi(dataApi: DocumentationStepApi): DocumentationStep {
    const instance = new DocumentationStep();
    instance.data = dataApi;
    instance.hasTable = false;
    instance.hasLongText = false;

    if (dataApi.argument != null && dataApi.argument.length > 0) {

      if (dataApi.argument.length > 1) {
        instance.hasTable = true;
      } else {
        instance.hasLongText = dataApi.argument[0].length === 1;
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
        for (const fragment of fragments) {
          instance.text.push(new DocumentationStepTextFragment(fragment, isParameter));
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

  data: DocumentationExamplesApi;
  table: DocumentationStepTable;

  static newFromApi(dataApi: DocumentationExamplesApi): DocumentationExamples {
    const instance = new DocumentationExamples();
    instance.data = dataApi;
    instance.table = new DocumentationStepTable();

    for (let i = 0; i < dataApi.tableHeader.length; i++) {
      instance.table.headerIds.push(i + '');
      instance.table.headers[i] = dataApi.tableHeader[i];
    }
    for (const line of dataApi.tableBody) {
      const row = new DocumentationStepRow();
      for (let i = 0; i < line.length; i++) {
        row.values[instance.table.headerIds[i]] = line[i];
      }
      instance.table.rows.push(row);
    }

    return instance;
  }

}

export class DocumentationScenario implements ExpandableNode {
  type = 'scenario';
  nodeId: string;
  localId: string;
  data: DocumentationScenarioApi;
  level: number;
  steps: Array<DocumentationStep>;
  examples: DocumentationExamples;

  static newFromApi(type: string, parentNodeId: string, dataApi: DocumentationScenarioApi, level: number): DocumentationScenario {
    const instance = new DocumentationScenario();
    instance.type = type;
    instance.data = dataApi;
    instance.localId = DocumentationNode.toAnchor(dataApi.name);
    instance.nodeId = `${instance.localId}`;
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
  type = 'feature';
  nodeId: string;
  localId: string;
  data: DocumentationFeatureApi;
  level: number;
  background: DocumentationScenario;
  scenarios: Array<DocumentationScenario>;
  children: Array<DocumentationScenario>;

  static newFromApi(parentNodeId: string, dataApi: DocumentationFeatureApi, level: number): DocumentationFeature {
    const instance = new DocumentationFeature();
    instance.data = dataApi;
    instance.localId = DocumentationNode.toAnchor(dataApi.path);
    instance.nodeId = `${instance.localId}`;
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
  data: DocumentationBranchApi;
  nodeId: string;
  localId: string;
  level: number;
  features: Array<DocumentationFeature>;

  static newFromApi(parentNodeId: string, dataApi: DocumentationBranchApi, level: number): DocumentationBranch {
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
  type = 'project';
  nodeId: string;
  localId: string;
  data: DocumentationProjectApi;
  level: number;
  branch: DocumentationBranch;

  static newFromApi(parentNodeId: string, dataApi: DocumentationProjectApi, level: number): DocumentationProject {
    const instance = new DocumentationProject();
    instance.data = dataApi;
    instance.localId = DocumentationNode.toAnchor(dataApi.name);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    // FIXME : for loop that overrides the same value. Why not use dataApi.branches[n] ?
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



