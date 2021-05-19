export interface OpenApiModelRow {
  title: string;
  openApiType: string;
  default: string;
  description: string;
  example: string;
}

export interface OpenApiModel {
  modelName: string;
  required: Array<string>;
  openApiRows: Array<OpenApiModelRow>;
  childrenModels: Array<OpenApiModel>;
  errors: Array<string>;
}

export interface OpenApiPath {
  openApiSpec: JSON;
  protocol: string;
  errors: Array<string>;
}
