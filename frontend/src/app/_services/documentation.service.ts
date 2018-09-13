import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";
import {HierarchyNodeApi} from "../_models/criterias";

@Injectable({
  providedIn: 'root'
})
export class HierarchyService {


  constructor(private http: HttpClient) {
  }

  criterias(): Observable<Array<HierarchyNodeApi>> {
    const url = `api/criterias`;
    return this.http.get<Array<HierarchyNodeApi>>(url);
  }



}
