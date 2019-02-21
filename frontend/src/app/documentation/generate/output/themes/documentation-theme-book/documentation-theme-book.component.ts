import {AfterViewChecked, Component, OnInit} from '@angular/core';
import {DocumentationNode, ExpandableNode} from '../../../../../_models/documentation';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {NestedTreeControl} from '@angular/cdk/tree';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';

@Component({
  selector: 'app-documentation-theme-book',
  templateUrl: './documentation-theme-book.component.html',
  styleUrls: ['./documentation-theme-book.component.scss']
})
export class DocumentationThemeBookComponent implements OnInit, AfterViewChecked {

  documentationData: Array<DocumentationNode>;

  nestedTreeControl: NestedTreeControl<ExpandableNode>;
  nestedDataSource: MatTreeNestedDataSource<DocumentationNode>;

  url: string;
  hash: string;

  constructor(private location: Location, private route: ActivatedRoute) {
  }

  ngOnInit() {
    this.route.url.subscribe(() => {
      this.url = this.location.path();
    });
    this.route.fragment.subscribe((hash: string) => {
      if (hash) {
        this.hash = hash;
      }
    });
  }

  updateGeneratedDocumentation(documentationData: Array<DocumentationNode>) {
    this.documentationData = documentationData;
    this.nestedTreeControl = new NestedTreeControl<ExpandableNode>(this.getChildren);
    this.nestedDataSource = new MatTreeNestedDataSource();
    this.nestedDataSource.data = this.documentationData;
    this.nestedTreeControl.dataNodes = this.documentationData;
    this.nestedTreeControl.expandAll();
  }


  ngAfterViewChecked() {
    this.selectHash();
  }

  selectHash() {
    if (this.hash) {
      const cmp = document.getElementById(this.hash);
      if (cmp) {
        cmp.scrollIntoView();
      }
    }
  }


  hasNestedChild = (_: number, nodeData: DocumentationNode) => nodeData.hasChilden();

  private getChildren = (node: ExpandableNode) => node.getChilden();


}
