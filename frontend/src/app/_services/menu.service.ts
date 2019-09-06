import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {DirectoryApi, HierarchyNodeApi, ProjectApi} from '../_models/hierarchy';
import {map} from 'rxjs/operators';
import {MenuDirectoryHierarchy, MenuHierarchy, MenuProjectHierarchy} from '../_models/menu';
import {UrlCleanerService} from './url-cleaner.service';
// import {of} from 'rxjs';
// import {MENU_SERVICE_RESPONSE} from '../test/test-data.spec';

@Injectable({
  providedIn: 'root'
})
export class MenuService {


  constructor(private http: HttpClient,
              private urlCleaner: UrlCleanerService) {
  }

  getMenuHeader(): Observable<HierarchyNodeApi> {
    const url = 'api/menu/header';
    return this.http.get<HierarchyNodeApi>(url);
  }

  getSubMenuForNode(nodeHierarchy: string): Observable<Array<MenuHierarchy>> {
    const url = `api/menu/submenu/${nodeHierarchy}`;
    return this.http.get<HierarchyNodeApi>(url)
      .pipe(
        map(submenu => this.buildMenuHierarchyForNode(submenu, 0))
      );
  }

  hierarchy(): Observable<HierarchyNodeApi> {
    const url = `api/menu`;
    return this.http.get<HierarchyNodeApi>(url);
    // return of(MENU_SERVICE_RESPONSE);
  }

  getMenuHierarchyForSelectedNode(nodeName: string): Observable<Array<MenuHierarchy>> {
    return this.getMenuForSelectedRootNode(nodeName)
      .pipe(
        map(node => this.buildMenuHierarchyForNode(node, 0))
      );
  }

  private getMenuForSelectedRootNode(nodeName: string): Observable<HierarchyNodeApi> {
    return this.hierarchy()
      .pipe(
        map((hierarchyNode: HierarchyNodeApi) => {
          return hierarchyNode.children.find(node => node.hierarchy === nodeName);
        })
      );
  }

  private buildMenuHierarchyForNode(node: HierarchyNodeApi, depth: number): Array<MenuHierarchy> {
    let children: Array<MenuHierarchy> = [];
    if (node.children) {
      children = node.children.map(c => this.buildMenuHierarchyForChild(c, depth));
    }
    let projects: Array<MenuHierarchy> = [];
    if (node.projects) {
      projects = node.projects.map(p => this.buildMenuHierarchyForProject(p, depth));
    }
    const menu: Array<MenuHierarchy> = [...children, ...projects];
    return menu;
  }

  private buildMenuHierarchyForChild(node: HierarchyNodeApi, depth: number): MenuHierarchy {
    const menu: MenuHierarchy = {
      name: node.slugName,
      label: node.name,
      type: 'Node',
      depth,
      children: this.buildMenuHierarchyForNode(node, depth + 1)
    };
    return menu;
  }

  private buildMenuHierarchyForProject(project: ProjectApi, depth: number): MenuHierarchy {
    let projectRoute = project.path;
    if (project.branches.length === 1) {
      projectRoute = project.branches[0].rootDirectory ? project.branches[0].rootDirectory.path : project.branches[0].path;
    } else {
      const stableBranch = project.branches.find(b => b.name === project.stableBranch);
      if (stableBranch) {
        projectRoute = stableBranch.rootDirectory ? stableBranch.rootDirectory.path : stableBranch.path;
      }
    }
    const menu: MenuProjectHierarchy = {
      name: project.id,
      label: project.label,
      type: 'Project',
      depth,
      route: this.urlCleaner.relativePathToUrl(projectRoute),
      stableBranch: project.stableBranch,
      children: this.buildMenuHierarchyForBranches(project, depth)
    };
    return menu;
  }

  private buildMenuHierarchyForBranches(project: ProjectApi, depth: number): Array<MenuHierarchy> {
    const branchesMenu = project.branches.map(b => {
      const branchItem: MenuHierarchy = {
        name: b.name,
        label: b.name,
        type: 'Branch',
        depth,
        route: this.urlCleaner.relativePathToUrl(b.path),
        children: b.rootDirectory && b.rootDirectory.children ? this.buildMenuHierarchyForRootDirectory(b.rootDirectory.children, depth + 1) : []
      };
      return branchItem;
    });
    return branchesMenu;
  }

  private buildMenuHierarchyForRootDirectory(directories: Array<DirectoryApi>, depth: number): Array<MenuDirectoryHierarchy> {
    return directories.map(d => {
      const directoryItem: MenuDirectoryHierarchy = {
        name: d.name,
        label: d.label,
        type: 'Directory',
        description: d.description,
        order: d.order,
        depth,
        route: this.urlCleaner.relativePathToUrl(d.path),
        children: this.buildMenuHierarchyForRootDirectory(d.children, depth + 1),
      };
      return directoryItem;
    });
  }

}
