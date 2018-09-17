export class DocumenationProjectApi {
  public id: string;
  public label: string;
  public stableBranch: string;
  public branches: Array<string>;
}

export class DocumentationNodeApi {
  public id: string;
  public slugName: string;
  public name: string;
  public children: Array<DocumentationNodeApi>;
  public childrenLabel: string;
  public childLabel: string;
  public projects: Array<DocumenationProjectApi>;
}
