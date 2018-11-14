import {Component, Input, OnInit, AfterViewInit, AfterViewChecked} from '@angular/core';
import {DocumentationNode, ExpandableNode} from "../../../../../_models/documentation";
import {MatTreeNestedDataSource} from "@angular/material/tree";
import {NestedTreeControl} from "@angular/cdk/tree";
import {ActivatedRoute, Params, Router, RouterEvent} from "@angular/router";
import { Location } from '@angular/common';
import {OnChanges, SimpleChanges} from "@angular/core/src/metadata/lifecycle_hooks";

@Component({
  selector: 'app-documentation-theme-book',
  templateUrl: './documentation-theme-book.component.html',
  styleUrls: ['./documentation-theme-book.component.scss']
})
export class DocumentationThemeBookComponent implements OnInit, AfterViewChecked {

  @Input()
  documentationData : DocumentationNode[];

  nestedTreeControl: NestedTreeControl<ExpandableNode>;
  nestedDataSource: MatTreeNestedDataSource<DocumentationNode>;

  url : string ;
  hash : string ;

  constructor(private location: Location, private route: ActivatedRoute, private router: Router) { }

  ngOnInit() {
    this.nestedTreeControl = new NestedTreeControl<ExpandableNode>(this._getChildren);
    this.nestedDataSource = new MatTreeNestedDataSource();
    this.nestedDataSource.data = this.documentationData;
    this.nestedTreeControl.dataNodes = this.documentationData;
    this.nestedTreeControl.expandAll();

    this.route.url.subscribe((url) => {
      this.url = this.location.path() ;
    });


    this.route.fragment.subscribe((hash: string) => {
        if (hash) {
          this.hash = hash;
        }
    });

  }

  ngAfterViewChecked(){
    this.selectHash();
  }

  selectHash(){
    if (this.hash ) {
      const cmp = document.getElementById(this.hash);
      if (cmp) {
        cmp.scrollIntoView();
      }
    }
  }


  hasNestedChild = (_: number, nodeData: DocumentationNode) => nodeData.hasChilden() ;

  private _getChildren = (node: ExpandableNode ) => node.getChilden();



}
