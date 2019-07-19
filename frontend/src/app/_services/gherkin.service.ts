import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {GherkinNode, GherkinNodeApi} from '../_models/gherkin';

@Injectable({
  providedIn: 'root'
})
export class GherkinService {


  constructor(private http: HttpClient) {
  }

  generateGherkin(httpParams: string): Observable<GherkinNodeApi> {
    const url = `api/gherkin?${httpParams}`;
    return this.http.get<GherkinNodeApi>(url);
  }

  decorate(apiResult: GherkinNodeApi): Array<GherkinNode> {
    const decoratedDataArray = [];
    for (const node of apiResult.children) {
      decoratedDataArray.push(GherkinNode.newFromApi('', node, 1));
    }
    return decoratedDataArray;
  }

}
