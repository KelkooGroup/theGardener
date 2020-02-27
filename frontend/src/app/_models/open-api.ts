import {JsonObject} from "@angular/compiler-cli/ngcc/src/packages/entry_point";

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
  errors: Array<string>
}


export interface OpenApiPath {
  openApiSpec: JSON;
  errors: Array<string>
}


export interface OpenApiPath {
  OpenApiSpec: JsonObject;
}
