import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";
import {HierarchyNodeApi} from "../_models/criterias";

@Injectable({
  providedIn: 'root'
})
export class DocumentationService {


  constructor(private http: HttpClient) {
  }

  generateDocumentation(params : string): Observable<Array<HierarchyNodeApi>> {
    const url = `api/generateDocumentation?${params}`;
    return this.http.get<Array<HierarchyNodeApi>>(url);
  }



}
