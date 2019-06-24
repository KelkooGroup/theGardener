import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {HierarchyNodeApi} from '../_models/hierarchy';
import {
  NavigationHierarchyNode,
  NavigationProject,
} from '../_models/navigation';

@Injectable({
  providedIn: 'root'
})
export class MenuService {


  constructor(private http: HttpClient) {
  }

  hierarchy(): Observable<HierarchyNodeApi> {
    const url = `api/menu`;
    return this.http.get<HierarchyNodeApi>(url);
  }

  buildHierarchyNodeSelector(apiResult: HierarchyNodeApi, path: string = ''): NavigationHierarchyNode {
    const currentNodeSelector = NavigationHierarchyNode.newFromApi(apiResult);
    if (apiResult.projects != null) {
      for (const loopProjectApi of apiResult.projects) {
        const currentProjectSelector = NavigationProject.newFromApi(loopProjectApi);
        currentNodeSelector.projects.push(currentProjectSelector);
        currentProjectSelector.relatedHierarchyNode = currentNodeSelector;
      }
    }
    currentNodeSelector.path = path;
    currentNodeSelector.open = true;
    currentNodeSelector.children = apiResult.children.map(child => this.buildHierarchyNodeSelector(child, `${path}_${child.slugName}`));
    currentNodeSelector.toBeDisplayed = true;

    return currentNodeSelector;
  }


}
