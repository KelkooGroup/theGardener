import {Component, Input, OnInit} from '@angular/core';
import {
  BranchSelector,
  HierarchyNodeSelector,
  ProjectSelector
} from "../../../../_services/criteriasSelection";
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {NestedTreeControl} from "@angular/cdk/tree";


@Component({
  selector: 'app-criterias-tree-selector',
  templateUrl: './criterias-tree-selector.component.html',
  styleUrls: ['./criterias-tree-selector.component.scss']
})
export class CriteriasTreeSelectorComponent  implements OnInit {

  nestedTreeControl: NestedTreeControl<HierarchyNodeSelector>;
  nestedDataSource: MatTreeNestedDataSource<HierarchyNodeSelector>;

  @Input()
  data : HierarchyNodeSelector[] ;

  @Input()
  childrenLabel : string ;

  ngOnInit() {
    this.nestedTreeControl = new NestedTreeControl<HierarchyNodeSelector>(this._getChildren);
    this.nestedDataSource = new MatTreeNestedDataSource();
    this.nestedDataSource.data = this.data;
  }

  hasNestedChild = (_: number, nodeData: HierarchyNodeSelector) => nodeData.hasChilden() || nodeData.hasProjects();

  private _getChildren = (node: HierarchyNodeSelector) => node.children;


  selectHierarchyNode(event) {
    var source = event.source.value as HierarchyNodeSelector;
    source.selection( event.checked );
    source.refreshIndeterminateStatus();
  }

  selectProject(event) {
    var source = event.source.value as ProjectSelector;
    source.selection( event.checked );
  }

  selectBranch(event) {
    var source = event.source.value as BranchSelector;
    source.selection();
  }
}
