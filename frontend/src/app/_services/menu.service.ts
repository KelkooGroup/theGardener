import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {DirectoryApi, HierarchyNodeApi, ProjectApi} from '../_models/hierarchy';
import {map} from 'rxjs/operators';
import {MenuDirectoryHierarchy, MenuHierarchy, MenuProjectHierarchy} from '../_models/menu';

@Injectable({
  providedIn: 'root'
})
export class MenuService {


  constructor(private http: HttpClient) {
  }

  hierarchy(): Observable<HierarchyNodeApi> {
    const url = `api/menu`;
    return this.http.get<HierarchyNodeApi>(url);
    // return of(MENU_SERVICE_RESPONSE);
  }

  getMenuForSelectedRootNode(nodeName: string): Observable<HierarchyNodeApi> {
    return this.hierarchy()
      .pipe(
        map((hierarchyNode: HierarchyNodeApi) => {
          return hierarchyNode.children.find(node => node.slugName === nodeName)
        })
      );
  }

  getMenuHierarchyForSelectedNode(nodeName: string): Observable<MenuHierarchy[]> {
    return this.getMenuForSelectedRootNode(nodeName)
      .pipe(
        map(node => this.buildMenuHierarchyForNode(node, 0))
      );
  }

  private buildMenuHierarchyForNode(node: HierarchyNodeApi, depth: number): MenuHierarchy[] {
    let children: Array<MenuHierarchy> = [];
    if (node.children) {
      children = node.children.map(c => this.buildMenuHierarchyForChild(c, depth))
    }
    let projects: Array<MenuHierarchy> = [];
    if (node.projects) {
      projects = node.projects.map(p => this.buildMenuHierarchyForProject(p, depth))
    }
    const menu: Array<MenuHierarchy> = [...children, ...projects];
    return menu;
  }

  private buildMenuHierarchyForChild(node: HierarchyNodeApi, depth: number): MenuHierarchy {
    const menu: MenuHierarchy = {
      name: node.slugName,
      label: node.name,
      type: 'Node',
      depth: depth,
      children: this.buildMenuHierarchyForNode(node, depth + 1)
    };
    return menu;
  }

  private buildMenuHierarchyForProject(project: ProjectApi, depth: number): MenuHierarchy {
    const menu: MenuProjectHierarchy = {
      name: project.id,
      label: project.label,
      type: 'Project',
      depth: depth,
      route: project.path,
      stableBranch: project.stableBranch,
      children: this.buildMenuHierarchyForBranches(project, depth + 1)
    };
    return menu;
  }

  private buildMenuHierarchyForBranches(project: ProjectApi, depth: number): MenuHierarchy[] {
    const branchesMenu = project.branches.map(b => {
      const branchItem: MenuHierarchy = {
        name: b.name,
        label: b.name,
        type: 'Branch',
        depth: depth,
        route: b.path,
        children: b.rootDirectory ? this.buildMenuHierarchyForRootDirectory([b.rootDirectory], depth+1): []
      };
      return branchItem
    });
    return branchesMenu;
  }

  private buildMenuHierarchyForRootDirectory(directories: DirectoryApi[], depth: number): MenuDirectoryHierarchy[] {
    return directories.map(d => {
      const rootDirectoryItem: MenuDirectoryHierarchy = {
        name: d.name,
        label: d.label,
        type: 'Directory',
        description: d.description,
        order: d.order,
        depth: depth,
        route: d.path,
        children: this.buildMenuHierarchyForRootDirectory(d.children, depth+1),
      };
      return rootDirectoryItem;
    });
  }

}
