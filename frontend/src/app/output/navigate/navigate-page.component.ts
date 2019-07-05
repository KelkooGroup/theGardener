import {AfterViewChecked, Component, OnInit, Output, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {MenuService} from '../../_services/menu.service';
import {ActivatedRoute, ParamMap} from '@angular/router';
import {HierarchyNodeApi} from '../../_models/hierarchy';
import {NavigationItem} from '../../_models/navigation';
import {NavigateContentComponent} from './navigate-content.component';
import {Location} from '@angular/common';
import {NavigateMenuComponent} from './navigate-menu/navigate-menu.component';
import {NotificationService} from '../../_services/notification.service';
import {concatMap} from "rxjs/operators";

@Component({
  selector: 'app-navigate-page',
  templateUrl: './navigate-page.component.html',
  styleUrls: ['./navigate-page.component.scss']
})
export class NavigatePageComponent implements OnInit, AfterViewChecked {

  @Output() items: Array<NavigationItem>;

  @ViewChildren(NavigateMenuComponent) roots: QueryList<NavigateMenuComponent>;

  @ViewChild(NavigateContentComponent, {static: true}) content: NavigateContentComponent;

  showProgressBar = false;

  selectionedItem: NavigationItem;

  initialPath: string;
  navigatedTo = false;
  currentPage: string;

  constructor(private hierarchyService: MenuService,
              private location: Location,
              private route: ActivatedRoute,
              private notificationService: NotificationService) {

  }

  selection(selection: NavigationItem) {
    if (this.selectionedItem) {
      this.selectionedItem.selected = false;
    }
    this.selectionedItem = selection;
    this.selectionedItem.selected = true;
    if (this.selectionedItem.toBeDisplayed) {
      this.showProgressBar = true;
      this.content.generateGherkin(this.selectionedItem.route);
      this.showProgressBar = false;
      const hash = window.location.hash;
      let path = `/app/documentation/navigate/${encodeURIComponent(this.selectionedItem.route)}`;
      if (hash && path.search('#') === -1) {
        path = `${path}${hash}`;
      }
      this.location.go(path);
    }
  }


  ngOnInit() {
    this.initialPath = this.route.snapshot.paramMap.get('path');
    this.route.paramMap.pipe(concatMap((params: ParamMap) => this.currentPage = params.get('name')), concatMap(() => this.hierarchyService.hierarchy())
    ).subscribe((result: HierarchyNodeApi) => {
      const hierarchyNodeSelectorTree = this.hierarchyService.buildHierarchyNodeSelector(result);
      this.items = this.searchChildren(hierarchyNodeSelectorTree && hierarchyNodeSelectorTree.children);
    }, error => {
      this.notificationService.showError('Error while getting items for navigation', error);
    });
  }

  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  ngAfterViewChecked() {
    if (!this.navigatedTo) {
      this.roots.forEach(root => {
        this.navigatedTo = true;
        (async () => {
          await this.delay(1);
          console.log(`Navigating to ${this.initialPath}`);
          root.navigateTo(this.initialPath);

        })();

      });
    }
  }

  private searchChildren(childs: Array<NavigationItem>) {
    let i = 0;
    while (i !== childs.length && childs[i].route !== this.currentPage) {
      i++;
    }
    return childs[i] && childs[i].itemChildren();
  }

}
