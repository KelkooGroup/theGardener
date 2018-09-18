import {Component, Input, OnInit} from '@angular/core';
import {DocumentationNode} from "../../../../../_models/documentation";
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {NestedTreeControl} from "@angular/cdk/tree";
import {HierarchyNodeSelector} from "../../../../../_services/criteriasSelection";

@Component({
  selector: 'app-documentation-theme-book',
  templateUrl: './documentation-theme-book.component.html',
  styleUrls: ['./documentation-theme-book.component.css']
})
export class DocumentationThemeBookComponent implements OnInit {

  @Input()
  documentationData : DocumentationNode[]

  nestedTreeControl: NestedTreeControl<DocumentationNode>;
  nestedDataSource: MatTreeNestedDataSource<DocumentationNode>;

  constructor() { }

  ngOnInit() {
    this.nestedTreeControl = new NestedTreeControl<DocumentationNode>(this._getChildren);
    this.nestedDataSource = new MatTreeNestedDataSource();
    this.nestedDataSource.data = this.documentationData;
    this.nestedTreeControl.dataNodes = this.documentationData;
    this.nestedTreeControl.expandAll();
  }

  hasNestedChild = (_: number, nodeData: DocumentationNode) => nodeData.hasChilden() || nodeData.hasProjects();

  private _getChildren = (node: DocumentationNode ) => node.children;



}
