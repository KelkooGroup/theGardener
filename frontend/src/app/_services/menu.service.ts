import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {DirectoryApi, HierarchyNodeApi, PageApi, ProjectApi} from '../_models/hierarchy';
import {map} from 'rxjs/operators';
import {
  MenuDirectoryHierarchy,
  MenuHierarchy,
  MenuPageHierarchy,
  MenuProjectHierarchy
} from '../_models/menu';
import {NavigationRoute} from '../_models/route';
import {RouteService} from './route.service';

@Injectable({
  providedIn: 'root'
})
export class MenuService {

  constructor(private http: HttpClient, private routeService: RouteService) {
  }

  getMenuHeader(): Observable<Array<MenuHierarchy>> {

    const url = 'api/menu/header';
    return this.http.get<HierarchyNodeApi>(url)
        .pipe(
            map(submenu => this.buildMenuHierarchyForNode({ nodes: [], directories: []}, submenu, 0))
        );
  }

  getSubMenuForNode(nodeHierarchy: string): Observable<Array<MenuHierarchy>> {
    const headerNode =  this.routeService.navigationParamsToNavigationRoute({nodes: nodeHierarchy}).nodes[0] ;
    const url = `api/menu/submenu/_${headerNode}`;
    return this.http.get<HierarchyNodeApi>(url)
      .pipe(
        map(submenu => this.buildMenuHierarchyForNode({ nodes: [headerNode], directories: []}, submenu, 0))
      );
  }

  hierarchy(): Observable<HierarchyNodeApi> {
    const url = `api/menu`;
    return this.http.get<HierarchyNodeApi>(url);
  }

  getMenuHierarchyForSelectedNode(nodeName: string): Observable<Array<MenuHierarchy>> {
    const head: NavigationRoute = { nodes: [nodeName], directories: []};
    return this.getMenuForSelectedRootNode(nodeName)
      .pipe(
        map(node => this.buildMenuHierarchyForNode( head , node, 0))
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

  private buildMenuHierarchyForNode(parentRoute: NavigationRoute, node: HierarchyNodeApi, depth: number): Array<MenuHierarchy> {
    let pages: Array<MenuHierarchy> = [];
    if (node.directory && node.directory.pages) {
      pages = this.buildMenuHierarchyForPagesAttachedToNode(parentRoute, node.directory,  node.directory.pages, depth);
    }
    let children: Array<MenuHierarchy> = [];
    if (node.children) {
      children = node.children.map(c => this.buildMenuHierarchyForChild(parentRoute, c, depth));
    }
    let projects: Array<MenuHierarchy> = [];
    if (node.projects) {
      projects = node.projects.map(p => this.buildMenuHierarchyForProject(parentRoute, p, depth));
    }
    const menu: Array<MenuHierarchy> = [...pages, ...children, ...projects];
    return menu;
  }

  private buildMenuHierarchyForChild(parentRoute: NavigationRoute, node: HierarchyNodeApi, depth: number): MenuHierarchy {
    const currentRoute = { nodes: parentRoute.nodes.concat(node.slugName) , directories: [] as Array<string>};
    const menu: MenuHierarchy = {
      name: node.slugName,
      label: node.name,
      type: 'Node',
      depth,
      route: currentRoute,
      children: this.buildMenuHierarchyForNode(currentRoute, node, depth + 1)
    };
    if (node.directory) {
      menu.directory = node.directory.path;
    }
    return menu;
  }

  private buildMenuHierarchyForProject(parentRoute: NavigationRoute, project: ProjectApi, depth: number): MenuHierarchy {
    const currentRoute = { nodes: parentRoute.nodes, project: project.id , directories: [] as Array<string>};
    const menu: MenuProjectHierarchy = {
      name: project.id,
      label: project.label,
      type: 'Project',
      depth,
      route: currentRoute,
      stableBranch: project.stableBranch,
      children: this.buildMenuHierarchyForBranches(currentRoute, project, depth)
    };
    return menu;
  }

  private buildMenuHierarchyForBranches(parentRoute: NavigationRoute, project: ProjectApi, depth: number): Array<MenuHierarchy> {

    const branchesMenu = project.branches.map(b => {
      const currentRoute = { nodes: parentRoute.nodes, project: project.id, branch: b.name , directories: [] as Array<string>};
      const branchItem: MenuHierarchy = {
        name: b.name,
        label: b.name,
        type: 'Branch',
        depth,
        route: currentRoute,
        children: b.rootDirectory && b.rootDirectory.children ? (this.buildMenuHierarchyForPages(currentRoute, b.rootDirectory.pages , depth + 1).concat( this.buildMenuHierarchyForDirectory(currentRoute, b.rootDirectory.children, depth + 1) ) ) : []
      };
      return branchItem;
    });
    return branchesMenu;
  }

  private buildMenuHierarchyForDirectory(parentRoute: NavigationRoute, directories: Array<DirectoryApi>, depth: number): Array<MenuDirectoryHierarchy> {
    return directories.map(d => {
      if (d.children.length == 0 && d.pages.length == 1 ) {
        const p = d.pages[0];
        const pageItem: MenuPageHierarchy = {
          name: p.name,
          label: p.label,
          type: 'Page',
          description: p.description,
          order: p.order,
          depth,
          route: { nodes: parentRoute.nodes, project: parentRoute.project, branch: parentRoute.branch, directories: parentRoute.directories.concat(d.name), page: p.name },
          children: []
        };
        return pageItem;
      } else {
        const currentRoute = { nodes: parentRoute.nodes, project: parentRoute.project, branch: parentRoute.branch, directories: parentRoute.directories.concat(d.name) };
        const directoryItem: MenuDirectoryHierarchy = {
          name: d.name,
          label: d.label,
          type: 'Directory',
          description: d.description,
          order: d.order,
          depth,
          route: currentRoute,
          children: this.buildMenuHierarchyForPages(currentRoute, d.pages, depth + 1).concat(this.buildMenuHierarchyForDirectory(currentRoute, d.children, depth + 1))
        };
        return directoryItem;
      }
    });
  }


  private buildMenuHierarchyForPagesAttachedToNode(parentRoute: NavigationRoute,  directory: DirectoryApi,  pages: Array<PageApi>, depth: number): Array<MenuPageHierarchy> {

    const directoryRoute = this.routeService.backEndPathToNavigationRoute(directory.path) ;

    return pages.map(p => {
      const pageItem: MenuPageHierarchy = {
        name: p.name,
        label: p.label,
        type: 'Page',
        description: p.description,
        order: p.order,
        depth,
        route:  { nodes: parentRoute.nodes, project: directoryRoute.project, branch: directoryRoute.branch, directories: directoryRoute.directories, page: p.name },
        children: []
      };
      return pageItem;
    });
  }

  private buildMenuHierarchyForPages(parentRoute: NavigationRoute, pages: Array<PageApi>, depth: number): Array<MenuPageHierarchy> {
    return pages.map(p => {
      const pageItem: MenuPageHierarchy = {
        name: p.name,
        label: p.label,
        type: 'Page',
        description: p.description,
        order: p.order,
        depth,
        route:  { nodes: parentRoute.nodes, project: parentRoute.project, branch: parentRoute.branch, directories: parentRoute.directories, page: p.name },
        children: []
      };
      return pageItem;
    });
  }


}
