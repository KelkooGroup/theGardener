
export interface OpenApiModelRow {
  title: string;
  type: string;
  default: string;
  description: string;
  example: string;
}

export interface OpenApiModel{
  model: Array<OpenApiModelRow>;
}

