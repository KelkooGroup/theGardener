import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {DocumentationNode, DocumentationNodeApi} from '../_models/documentation';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DocumentationService {


  constructor(private http: HttpClient) {
  }

  generateDocumentation(httpParams: string): Observable<Array<DocumentationNode>> {
    const url = `api/generateDocumentation?${httpParams}`;
    return this.http.get<DocumentationNodeApi>(url)
      .pipe(
        map(res => DocumentationService._decorate(res))
      );
  }

  static _decorate(apiResult: DocumentationNodeApi): Array<DocumentationNode> {
    const decoratedDataArray = [];
    for (const node of apiResult.children) {
      decoratedDataArray.push(DocumentationNode.newFromApi('', node, 1));
    }
    return decoratedDataArray;
  }


}
