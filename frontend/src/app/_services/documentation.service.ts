import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {DocumentationNode, DocumentationNodeApi} from '../_models/documentation';

@Injectable({
  providedIn: 'root'
})
export class DocumentationService {


  constructor(private http: HttpClient) {
  }

  generateDocumentation(httpParams: string): Observable<DocumentationNodeApi> {
    const url = `api/generateDocumentation?${httpParams}`;
    return this.http.get<DocumentationNodeApi>(url);
  }

  public decorate(apiResult: DocumentationNodeApi): Array<DocumentationNode> {
    const decoratedDataArray = [];
    for (const node of apiResult.children) {
      decoratedDataArray.push(DocumentationNode.newFromApi('', node, 1));
    }
    return decoratedDataArray;
  }


}
