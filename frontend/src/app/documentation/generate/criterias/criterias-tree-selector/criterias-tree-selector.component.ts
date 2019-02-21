import {Component, Input, OnInit} from '@angular/core';
import {HierarchyNodeSelector, ProjectSelector} from '../../../../_services/criterias-selection';
import {MatCheckboxChange} from '@angular/material';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {NestedTreeControl} from '@angular/cdk/tree';


@Component({
  selector: 'app-criterias-tree-selector',
  templateUrl: './criterias-tree-selector.component.html',
  styleUrls: ['./criterias-tree-selector.component.scss']
})
export class CriteriasTreeSelectorComponent implements OnInit {

  nestedTreeControl: NestedTreeControl<HierarchyNodeSelector>;
  nestedDataSource: MatTreeNestedDataSource<HierarchyNodeSelector>;

  @Input() data: Array<HierarchyNodeSelector>;

  @Input() childrenLabel: string;

  ngOnInit() {
    this.nestedTreeControl = new NestedTreeControl<HierarchyNodeSelector>(this.getChildren);
    this.nestedDataSource = new MatTreeNestedDataSource();
    this.nestedDataSource.data = this.data;
  }

  hasNestedChild = (_: number, nodeData: HierarchyNodeSelector) => nodeData.hasChilden() || nodeData.hasProjects();

  private getChildren = (node: HierarchyNodeSelector) => node.children;


  selectHierarchyNode(event: MatCheckboxChange) {
    const source = event.source.value as unknown as HierarchyNodeSelector;
    source.selection(event.checked);
    source.refreshIndeterminateStatus();
  }

  selectProject(event: MatCheckboxChange) {
    const source = event.source.value as unknown as ProjectSelector;
    source.selection(event.checked);
  }


}
