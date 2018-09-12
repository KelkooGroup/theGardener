import {Component, EventEmitter, Injectable, Input, Output} from '@angular/core';
import {BehaviorSubject} from "rxjs/index";
import {
  BranchSelector,
  CriteriasSelector,
  HierarchyNodeSelector,
  ProjectSelector
} from "../../../../_models/criteriasSelection";
import {HierarchyNodeApi} from "../../../../_models/criterias";
import {HierarchyService} from "../../../../_services/hierarchy.service";
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {NestedTreeControl} from "@angular/cdk/tree";


@Injectable()
export class HierarchyProvider {
  dataChange = new BehaviorSubject<HierarchyNodeSelector[]>([]);

  get data(): HierarchyNodeSelector[] {
    return this.dataChange.value;
  }

  constructor(private hierarchyService: HierarchyService) {
    this.hierarchyService.criterias().subscribe(
      (result: Array<HierarchyNodeApi>) => {
        const root: HierarchyNodeSelector = HierarchyNodeSelector.buildHierarchyNodeSelectorAsTree(HierarchyNodeSelector.buildHierarchyNodeSelector(result))
        this.dataChange.next(root.children);
      },
      err => {
      });
  }
}


@Component({
  selector: 'app-criterias-tree-selector',
  templateUrl: './criterias-tree-selector.component.html',
  styleUrls: ['./criterias-tree-selector.component.css'],
  providers: [HierarchyProvider]
})
export class CriteriasTreeSelectorComponent {

  @Output()
  onHierarchyProvided: EventEmitter<HierarchyNodeSelector[]> = new EventEmitter();

  nestedTreeControl: NestedTreeControl<HierarchyNodeSelector>;
  nestedDataSource: MatTreeNestedDataSource<HierarchyNodeSelector>;


  constructor(hierarchyProvider: HierarchyProvider) {
    this.nestedTreeControl = new NestedTreeControl<HierarchyNodeSelector>(this._getChildren);
    this.nestedDataSource = new MatTreeNestedDataSource();
    hierarchyProvider.dataChange.subscribe(data => this.nestedDataSource.data = data);
    hierarchyProvider.dataChange.subscribe(data => this.onHierarchyProvided.emit(data));
  }

  hasNestedChild = (_: number, nodeData: HierarchyNodeSelector) => nodeData.hasChilden() || nodeData.hasProjects();

  private _getChildren = (node: HierarchyNodeSelector) => node.children;


  selectHierarchyNode(event) {
    var source = event.source.value as HierarchyNodeSelector;
    source.selected = event.checked;
  }

  selectProject(event) {
    var source = event.source.value as ProjectSelector;
    source.selected = event.checked;
  }

  selectBranch(event) {
    var source = event.source.value as BranchSelector;
    source.project.selectedBranch = source;
  }
}
