
export interface Scenario {
  id: string;
  branchId: string;
  path: string;
  background?: GherkinBackground;
  tags: Array<string>;
  language: string;
  keyword: string; // FIXME
  name: string;
  description: string;
  scenarios: Array<GherkinScenario>;
  comments: Array<string>; // FIXME: do we keep it ?
}

export interface GherkinBackground {
  id: string;
  keyword: string; // FIXME (is it an enum ?)
  name: string; // FIXME: do we keep it ?
  description: string; // FIXME: do we keep it ?
  steps: Array<GherkinStep>;

}

export interface GherkinScenario {
  id: string;
  keyword: string; // FIXME: do we keep it ?
  name: string;
  description: string;
  tags: Array<string>;
  abstractionLevel: string;
  caseType: string;
  steps: Array<GherkinStep>;
  examples?: Array<GherkinExamples>;
  workflowStep: string; // FIXME: any meaning on frontend side ?
}

export interface GherkinStep {
  id: string;
  keyword: string; // FIXME (is it an enum ?)
  text: string;
  argument: Array<Array<string>>;
}

export interface GherkinExamples {
  id: string;
  tags: Array<string>;
  keyword: string;
  description: string;
  tableHeader: Array<string>;
  tableBody: Array<Array<string>>;
}

export interface GherkinTable {
  tableHeader: Array<string>;
  tableBody: Array<Array<string>>;
}

export interface GherkinTextFragment {
  text: string;
  isParameter: boolean;
}

