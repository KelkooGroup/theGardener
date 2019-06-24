import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {HierarchyNodeApi} from '../_models/hierarchy';
import {
  NavigationHierarchyNode,
  NavigationProject,
} from '../_models/navigation';
import {map} from 'rxjs/operators';

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

  hierarchy(): Observable<NavigationHierarchyNode> {
    const url = `api/menu`;
    return this.http.get<Array<HierarchyNodeApi>>(url)
      .pipe(
        map(r => this.buildHierarchyNodeSelector(r)),
        map(r => this.buildHierarchyNodeSelectorAsTree(r)),
      );
  }

  buildHierarchyNodeSelector(apiResult: Array<HierarchyNodeApi>): Array<NavigationHierarchyNode> {
    const hierarchyNodeSelectorArray = [];
    for (const loopNodeApi of apiResult) {
      const currentNodeSelector = NavigationHierarchyNode.newFromApi(loopNodeApi);
      hierarchyNodeSelectorArray.push(currentNodeSelector);
      if (loopNodeApi.projects != null) {
        for (const loopProjectApi of loopNodeApi.projects) {
          const currentProjectSelector = NavigationProject.newFromApi(loopProjectApi);
          currentNodeSelector.projects.push(currentProjectSelector);
          currentProjectSelector.relatedHierarchyNode = currentNodeSelector;
        }
      }
    }
    return hierarchyNodeSelectorArray;
  }


  buildHierarchyNodeSelectorAsTree(listNode: Array<NavigationHierarchyNode>): NavigationHierarchyNode {
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
      for (const loopNode of  children) {
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
      for (const loopTaken of taken) {
        const tuple = this.build(loopTaken, Object.assign([], left), root, node);
        loopTaken.children = tuple.taken;
      }
    }
    return new TupleHierarchyNodeSelector(taken, left);
  }

}
