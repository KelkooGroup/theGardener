import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";
import {HierarchyNodeApi} from "../_models/criterias";
import {DocumentationNode, DocumentationNodeApi, DocumentationProject} from "../_models/documentation";
import {HierarchyNodeSelector, ProjectSelector} from "./criteriasSelection";

@Injectable({
  providedIn: 'root'
})
export class DocumentationService {


  constructor(private http: HttpClient) {
  }

  generateDocumentation(httpParams : string): Observable<DocumentationNodeApi> {
    const url = `api/generateDocumentation?${httpParams}`;
    return this.http.get<DocumentationNodeApi>(url);
  }

  public  decorate(apiResult: DocumentationNodeApi): Array<DocumentationNode> {
    var decoratedDataArray = new Array<DocumentationNode>();
    for (let node of apiResult.children) {
      decoratedDataArray.push( DocumentationNode.newFromApi(node,1) )  ;
    }
    return decoratedDataArray;
  }



}
