import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";
import {HierarchyNodeApi} from "../_models/criterias";
import {DocumentationNodeApi} from "../_models/documentation";

@Injectable({
  providedIn: 'root'
})
export class DocumentationService {


  constructor(private http: HttpClient) {
  }

  generateDocumentation(httpParams : string): Observable<Array<DocumentationNodeApi>> {
    const url = `api/generateDocumentation?${httpParams}`;
    return this.http.get<Array<DocumentationNodeApi>>(url);
  }



}
