import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {HierarchyNodeApi} from '../_models/hierarchy';
import {
  NavigationHierarchyNode,
  NavigationProject,
} from '../_models/navigation';

class TupleHierarchyNodeSelector {

  constructor(
    public taken: Array<NavigationHierarchyNode>,
    public left: Array<NavigationHierarchyNode>
  ) {
  }
}


@Injectable({
  providedIn: 'root'
})
export class HierarchyService {


  constructor(private http: HttpClient) {
  }

  public hierarchy(): Observable<Array<HierarchyNodeApi>> {
    const url = `api/criterias`;
    return this.http.get<Array<HierarchyNodeApi>>(url);
  }

  buildHierarchyNodeSelector(apiResult: Array<HierarchyNodeApi>): Array<NavigationHierarchyNode> {
    const hierarchyNodeSelectorArray = [];
    for (let i = 0; i < apiResult.length; i++) {
      const loopNodeApi = apiResult[i];
      const currentNodeSelector = NavigationHierarchyNode.newFromApi(loopNodeApi);
      hierarchyNodeSelectorArray.push(currentNodeSelector);
      if (loopNodeApi.projects != null) {
        for (let j = 0; j < loopNodeApi.projects.length; j++) {
          const loopProjectApi = loopNodeApi.projects[j];
          const currentProjectSelector = NavigationProject.newFromApi(loopProjectApi);
          currentNodeSelector.projects.push(currentProjectSelector);
          currentProjectSelector.relatedHierarchyNode = currentNodeSelector;
        }
      }
    }
    return hierarchyNodeSelectorArray;
  }


  public buildHierarchyNodeSelectorAsTree(listNode: Array<NavigationHierarchyNode>): NavigationHierarchyNode {
    let root: NavigationHierarchyNode = null;
    if (listNode.length > 0) {
      root = listNode[0];
      root.path = '';
      root.open = true;
      const tuple = this.build(root, listNode, root, root);
      root.children = tuple.taken;
    }
    return root;
  }


  private build(node: NavigationHierarchyNode, children: Array<NavigationHierarchyNode>, root: NavigationHierarchyNode, parent: NavigationHierarchyNode): TupleHierarchyNodeSelector {

    const taken = [];
    const left = [];

    if (children.length > 0) {
      for (let i = 0; i < children.length; i++) {
        const loopNode = children[i];
        if (loopNode.id.startsWith(node.id)) {
          loopNode.root = root;
          if (loopNode.id.length === node.id.length + 3) {
            loopNode.path = `${node.path}_${loopNode.slugName}`;
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
        const loopTaken = taken[i];
        const tuple = this.build(loopTaken, Object.assign([], left), root,node);
        loopTaken.children = tuple.taken;
      }
    }
    return new TupleHierarchyNodeSelector(taken, left);
  }

}
