import {AfterViewChecked, Component, OnInit, Output, ViewChild} from '@angular/core';
import {CriteriasService} from "../../_services/criterias.service";
import {ActivatedRoute} from "@angular/router";
import {HierarchyNodeApi} from "../../_models/criterias";
import {CriteriasSelector, HierarchyNodeSelector, NavigationItem} from "../../_services/criterias-selection";
import {NavigateContentComponent} from "./navigate-content.component";
import {Location} from "@angular/common";
import {NavigateTreeComponent} from "./navigate-tree/navigate-tree.component";
import {NavigateMenuComponent} from "./navigate-menu/navigate-menu.component";

@Component({
  selector: 'app-navigate-page',
  templateUrl: './navigate-page.component.html',
  styleUrls: ['./navigate-page.component.scss']
})
export class NavigatePageComponent implements OnInit, AfterViewChecked {

  @Output()
  items: HierarchyNodeSelector[];

  @Output()
  criteriasSelector = new CriteriasSelector();

  @ViewChild(NavigateMenuComponent)
  root: NavigateMenuComponent;

  @ViewChild(NavigateTreeComponent)
  tree: NavigateTreeComponent;

  @ViewChild(NavigateContentComponent)
  content: NavigateContentComponent;

  showProgressBar= false;

  selectionedItem: NavigationItem;

  initialPath: string;
  navigatedTo = false;

  constructor(private criteriasService: CriteriasService,  private location: Location, private route: ActivatedRoute) {
    this.criteriasService.criterias().subscribe(
      (result: Array<HierarchyNodeApi>) => {
        const hierarchyNodeSelectorTree = criteriasService.buildHierarchyNodeSelectorAsTree(criteriasService.buildHierarchyNodeSelector(result));
        this.items = hierarchyNodeSelectorTree && hierarchyNodeSelectorTree.children;
      },
      () => {
      });
  }

  selection(selection: NavigationItem) {
    if (this.selectionedItem) {
      this.selectionedItem.selected = false;
    }
    this.selectionedItem = selection;
    this.selectionedItem.selected = true;
    if (   this.selectionedItem.toBeDisplayed ) {
      this.showProgressBar = true ;
      this.content.generateDocumentation(this.selectionedItem.route);
      this.showProgressBar = false ;
      var hash = window.location.hash
      var path = `/app/documentation/navigate/${encodeURIComponent(this.selectionedItem.route)}`;
      if (hash){
        path=`${path}#${hash}`;
      }
      this.location.go(path);
    }
  }



  ngOnInit() {
    this.initialPath = this.route.snapshot.paramMap.get("path")
  }

  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  ngAfterViewChecked() {
    if (!this.navigatedTo && this.root) {
      this.navigatedTo = true;
      (async () => {
        await this.delay(1);
        console.log(`Navigating to ${this.initialPath}`);
        this.root.navigateTo(this.initialPath);

      })();
    }
  }



}
