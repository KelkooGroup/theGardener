import {Component, OnInit} from '@angular/core';
import {MenuPageHierarchy} from '../../../_models/menu';

@Component({
  selector: 'app-navigate-content',
  templateUrl: './navigate-content.component.html',
  styleUrls: ['./navigate-content.component.scss']
})
export class NavigateContentComponent implements OnInit {

  pages: Array<MenuPageHierarchy>;

  constructor() {
  }

  ngOnInit() {
    const page1: MenuPageHierarchy = {
      name: 'context',
      label: 'Context',
      route: 'context',
      type: 'Page',
      depth: 4,
      order: 0,
      children: [],
    };
    const page2: MenuPageHierarchy = {
      name: 'constraints',
      label: 'Constraints',
      route: 'constraints',
      type: 'Page',
      depth: 4,
      order: 2,
      children: [],
    };
    const page3: MenuPageHierarchy = {
      name: 'hierarchy',
      label: 'Hierarchy',
      route: 'hierarchy',
      type: 'Page',
      depth: 4,
      order: 1,
      children: [],
    }

    this.pages = [page1, page2, page3];
  }

  trackByPage(index: number, item: MenuPageHierarchy) {
    return item.name;
  }

  get orderedPages() {
    return this.pages.sort((p1, p2) => p1.order > p2.order ? 1 : p1.order === p2.order ? 0 : -1);
  }
}
