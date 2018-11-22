import {Injectable} from '@angular/core';
import {Observable} from "rxjs/Observable";
import {HttpClient} from "@angular/common/http";
import {HierarchyNodeApi} from "../_models/criterias";
import {
  CriteriasSelector,
  HierarchyNodeSelector,
  ProjectSelector,
  TupleHierarchyNodeSelector
} from "./criteriasSelection";


@Injectable({
  providedIn: 'root'
})
export class CriteriasService {


  constructor(private http: HttpClient) {
  }

  public criterias(): Observable<Array<HierarchyNodeApi>> {
    const url = `api/criterias`;
    return this.http.get<Array<HierarchyNodeApi>>(url);
  }

  static buildHierarchyNodeSelector(apiResult: Array<HierarchyNodeApi>): Array<HierarchyNodeSelector> {
    let hierarchyNodeSelectorArray = [];
    for (let i = 0; i < apiResult.length; i++) {
      let loopNodeApi = apiResult[i];
      let currentNodeSelector = HierarchyNodeSelector.newFromApi(loopNodeApi);
      hierarchyNodeSelectorArray.push(currentNodeSelector);
      if (loopNodeApi.projects != null) {
        for (let j = 0; j < loopNodeApi.projects.length; j++) {
          let loopProjectApi = loopNodeApi.projects[j];
          let currentProjectSelector = ProjectSelector.newFromApi(loopProjectApi);
          currentNodeSelector.projects.push(currentProjectSelector);
          currentProjectSelector.relatedHierarchyNode = currentNodeSelector;
        }
      }
    }
    return hierarchyNodeSelectorArray;
  }


  public buildHierarchyNodeSelectorAsTree(listNode: Array<HierarchyNodeSelector>): HierarchyNodeSelector {
    let root: HierarchyNodeSelector = null;
    if (listNode.length > 0) {
      root = listNode[0];
      root.path = "";
      root.open = true;
      let tuple = this.build(root, listNode, root);
      root.children = tuple.taken;
    }
    return root;
  }


  private build(node: HierarchyNodeSelector, children: Array<HierarchyNodeSelector>, root: HierarchyNodeSelector): TupleHierarchyNodeSelector {

    let taken = [];
    let left = [];

    if (children.length > 0) {
      for (let i = 0; i < children.length; i++) {
        let loopNode = children[i];
        if (loopNode.id.startsWith(node.id)) {
          loopNode.root = root;
          if (loopNode.id.length == node.id.length + 3) {
            loopNode.path = `${node.path}${CriteriasSelector.SEP_PATH}${loopNode.slugName}`;
            taken.push(loopNode);
          }
          if (loopNode.id.length > node.id.length + 3) {
            left.push(loopNode);
          }
        }
      }
    }
    if (taken.length > 0) {
      for (let i = 0; i < taken.length; i++) {
        let loopTaken = taken[i];
        let tuple = this.build(loopTaken, Object.assign([], left), root);
        loopTaken.children = tuple.taken;
      }
    }
    return new TupleHierarchyNodeSelector(taken, left);
  }

}
