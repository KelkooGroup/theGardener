export interface MenuHierarchy {
  name: string;
  label: string;
  type: 'Project' | 'Node' | 'Branch' | 'Directory' | 'Page';
  depth: number;
  route?: string;
  children: Array<MenuHierarchy | MenuProjectHierarchy>;
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
}
