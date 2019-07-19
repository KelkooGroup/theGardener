export class GherkinStepApi {
  id: string;
  keyword: string;
  text: string;
  argument: Array<Array<string>>;
}

export class GherkinExamplesApi {
  id: string;
  keyword: string;
  description: string;
  tableBody: Array<Array<string>>;
  tableHeader: Array<string>;
}

export class GherkinScenarioApi {
  id: string;
  name: string;
  abstractionLevel: string;
  caseType: string;
  workflowStep: string;
  keyword: string;
  description: string;
  tags: Array<string>;
  steps: Array<GherkinStepApi>;
  examples: Array<GherkinExamplesApi>;
}

export class GherkinFeatureApi {
  id: string;
  path: string;
  name: string;
  description: string;
  tags: Array<string>;
  comments: Array<string>;
  keyword: string;
  background: GherkinScenarioApi;
  scenarios: Array<GherkinScenarioApi>;
}

export class GherkinBranchApi {
  id: string;
  name: string;
  isStable: string;
  features: Array<GherkinFeatureApi>;
}

export class GherkinProjectApi {
  id: string;
  name: string;
  stableBranch: string;
  branches: Array<GherkinBranchApi>;
}

export class GherkinNodeApi {
  id: string;
  slugName: string;
  name: string;
  childrenLabel: string;
  childLabel: string;
  children: Array<GherkinNodeApi>;
  projects: Array<GherkinProjectApi>;
}

export interface ExpandableNode {

  type: string;
  nodeId: string;
  localId: string;

  getChilden(): Array<ExpandableNode>;

  hasChilden(): boolean;
}

export class GherkinNode implements ExpandableNode {
  type = 'node';
  nodeId: string;
  localId: string;
  data: GherkinNodeApi;
  level: number;
  children: Array<GherkinNode>;
  projects: Array<GherkinProject>;

  static toAnchor(id: string): string {
    return id.split(' ').join('_').split('/').join('-').split('.').join('-').split('#').join('-').split(':').join('-').toLocaleLowerCase();
  }

  static newFromApi(parentNodeId: string, dataApi: GherkinNodeApi, level: number): GherkinNode {
    const instance = new GherkinNode();
    instance.data = dataApi;
    instance.localId = GherkinNode.toAnchor(dataApi.slugName);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    instance.children = [];
    for (const child of dataApi.children) {
      instance.children.push(GherkinNode.newFromApi(instance.nodeId, child, level + 1));
    }
    instance.projects = [];
    for (const project of dataApi.projects) {
      instance.projects.push(GherkinProject.newFromApi(instance.nodeId, project, level + 1));
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

export class GherkinStepRow {
  values: { [key: string]: string; } = {};

  getValue(key: string): string {
    return this.values[key];
  }
}

export class GherkinStepTable {
  headers: { [key: string]: string; } = {};
  headerIds: Array<string> = [];
  rows: Array<GherkinStepRow> = [];

  getHeader(headerId: string): string {
    return this.headers[headerId];
  }
}


export class GherkinStepTextFragment {
  constructor(public text: string, public isParameter: boolean) {
  }
}

export class GherkinStep implements ExpandableNode {
  type: string;
  nodeId: string;
  localId: string;
  data: GherkinStepApi;
  text: Array<GherkinStepTextFragment> = [];
  hasTable: boolean;
  table: GherkinStepTable;
  hasLongText: boolean;
  longText: string;


  static newFromApi(dataApi: GherkinStepApi): GherkinStep {
    const instance = new GherkinStep();
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
      instance.table = new GherkinStepTable();
      for (let j = 0; j < dataApi.argument.length; j++) {
        const currentRowValues = dataApi.argument[j];
        if (j === 0) {
          for (let l = 0; l < currentRowValues.length; l++) {
            instance.table.headerIds.push(l + '');
            instance.table.headers[l] = currentRowValues[l];
          }
        } else {
          const row = new GherkinStepRow();
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
        instance.text.push(new GherkinStepTextFragment(instance.data.text, false));
      } else {
        let isParameter = false;
        for (const fragment of fragments) {
          instance.text.push(new GherkinStepTextFragment(fragment, isParameter));
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

export class GherkinExamples {

  data: GherkinExamplesApi;
  table: GherkinStepTable;

  static newFromApi(dataApi: GherkinExamplesApi): GherkinExamples {
    const instance = new GherkinExamples();
    instance.data = dataApi;
    instance.table = new GherkinStepTable();

    for (let i = 0; i < dataApi.tableHeader.length; i++) {
      instance.table.headerIds.push(i + '');
      instance.table.headers[i] = dataApi.tableHeader[i];
    }
    for (const line of dataApi.tableBody) {
      const row = new GherkinStepRow();
      for (let i = 0; i < line.length; i++) {
        row.values[instance.table.headerIds[i]] = line[i];
      }
      instance.table.rows.push(row);
    }

    return instance;
  }

}

export class GherkinScenario implements ExpandableNode {
  type = 'scenario';
  nodeId: string;
  localId: string;
  data: GherkinScenarioApi;
  level: number;
  steps: Array<GherkinStep>;
  examples: GherkinExamples;

  static newFromApi(type: string, parentNodeId: string, dataApi: GherkinScenarioApi, level: number): GherkinScenario {
    const instance = new GherkinScenario();
    instance.type = type;
    instance.data = dataApi;
    instance.localId = GherkinNode.toAnchor(dataApi.name);
    instance.nodeId = `${instance.localId}`;
    instance.level = level;
    instance.steps = [];
    for (const step of dataApi.steps) {
      instance.steps.push(GherkinStep.newFromApi(step));
    }
    if (dataApi.examples && dataApi.examples.length > 0) {
      instance.examples = GherkinExamples.newFromApi(dataApi.examples[0]);
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


export class GherkinFeature implements ExpandableNode {
  type = 'feature';
  nodeId: string;
  localId: string;
  data: GherkinFeatureApi;
  level: number;
  background: GherkinScenario;
  scenarios: Array<GherkinScenario>;
  children: Array<GherkinScenario>;

  static newFromApi(parentNodeId: string, dataApi: GherkinFeatureApi, level: number): GherkinFeature {
    const instance = new GherkinFeature();
    instance.data = dataApi;
    instance.localId = GherkinNode.toAnchor(dataApi.path);
    instance.nodeId = `${instance.localId}`;
    instance.level = level;
    if (dataApi.background) {
      instance.background = GherkinScenario.newFromApi('background', instance.nodeId + '_background', dataApi.background, level + 1);
    }

    instance.scenarios = [];
    for (const scenario of dataApi.scenarios) {
      instance.scenarios.push(GherkinScenario.newFromApi('scenario', instance.nodeId, scenario, level + 1));
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

export class GherkinBranch {
  data: GherkinBranchApi;
  nodeId: string;
  localId: string;
  level: number;
  features: Array<GherkinFeature>;

  static newFromApi(parentNodeId: string, dataApi: GherkinBranchApi, level: number): GherkinBranch {
    const instance = new GherkinBranch();
    instance.data = dataApi;
    instance.localId = GherkinNode.toAnchor(dataApi.name);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    instance.features = [];
    for (const feature of dataApi.features) {
      instance.features.push(GherkinFeature.newFromApi(instance.nodeId, feature, level + 1));
    }
    return instance;
  }
}


export class GherkinProject implements ExpandableNode {
  type = 'project';
  nodeId: string;
  localId: string;
  data: GherkinProjectApi;
  level: number;
  branch: GherkinBranch;

  static newFromApi(parentNodeId: string, dataApi: GherkinProjectApi, level: number): GherkinProject {
    const instance = new GherkinProject();
    instance.data = dataApi;
    instance.localId = GherkinNode.toAnchor(dataApi.name);
    instance.nodeId = `${parentNodeId}_${instance.localId}`;
    instance.level = level;
    for (const branch of dataApi.branches) {
      instance.branch = GherkinBranch.newFromApi(instance.nodeId, branch, level);
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



