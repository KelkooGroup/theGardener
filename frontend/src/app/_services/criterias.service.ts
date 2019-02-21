import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {HierarchyNodeApi} from '../_models/criterias';
import {
  CriteriasSelector,
  HierarchyNodeSelector,
  ProjectSelector,
  TupleHierarchyNodeSelector
} from './criterias-selection';


@Injectable({
  providedIn: 'root'
})
export class CriteriasService {


  constructor(private http: HttpClient) {
  }

  criterias(): Observable<Array<HierarchyNodeApi>> {
    const url = `api/criterias`;
    return this.http.get<Array<HierarchyNodeApi>>(url);
  }

  buildHierarchyNodeSelector(apiResult: Array<HierarchyNodeApi>): Array<HierarchyNodeSelector> {
    const hierarchyNodeSelectorArray = [];
    for (const loopNodeApi of apiResult) {
      const currentNodeSelector = HierarchyNodeSelector.newFromApi(loopNodeApi);
      hierarchyNodeSelectorArray.push(currentNodeSelector);

      if (loopNodeApi.projects != null) {
        for (const loopProjectApi of loopNodeApi.projects) {
          const currentProjectSelector = ProjectSelector.newFromApi(loopProjectApi);
          currentNodeSelector.projects.push(currentProjectSelector);
          currentProjectSelector.relatedHierarchyNode = currentNodeSelector;
        }
      }
    }
    return hierarchyNodeSelectorArray;
  }


  buildHierarchyNodeSelectorAsTree(listNode: Array<HierarchyNodeSelector>): HierarchyNodeSelector {
    let root: HierarchyNodeSelector = null;
    if (listNode.length > 0) {
      root = listNode[0];
      root.path = '';
      root.open = true;
      const tuple = this.build(root, listNode, root);
      root.children = tuple.taken;
    }
    return root;
  }


  private build(node: HierarchyNodeSelector, children: Array<HierarchyNodeSelector>, root: HierarchyNodeSelector): TupleHierarchyNodeSelector {

    const taken = [];
    const left = [];

    if (children.length > 0) {
      for (const loopNode of children) {
        if (loopNode.id.startsWith(node.id)) {
          loopNode.root = root;
          if (loopNode.id.length === node.id.length + 3) {
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
      for (const loopTaken of taken) {
        const tuple = this.build(loopTaken, Object.assign([], left), root);
        loopTaken.children = tuple.taken;
      }
    }
    return new TupleHierarchyNodeSelector(taken, left);
  }

}
