import {NavigationRoute} from './route';


export interface MenuHierarchy {
  name: string;
  label: string;
  type: string; // 'Node' | 'Project' | 'Branch' | 'Directory' | 'Page';
  depth: number;
  route: NavigationRoute;
  children: Array<MenuHierarchy | MenuProjectHierarchy | MenuDirectoryHierarchy | MenuPageHierarchy>;
  directory?: string;
}

export interface MenuProjectHierarchy extends MenuHierarchy {
  stableBranch: string;
}

export interface MenuDirectoryHierarchy extends MenuHierarchy {
  order: number;
  description: string;
}

export interface MenuPageHierarchy extends MenuHierarchy {
  order: number;
  description: string;
}
