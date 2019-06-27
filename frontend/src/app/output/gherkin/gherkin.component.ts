import {AfterViewChecked, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {GherkinNode, ExpandableNode} from '../../_models/gherkin';
import {MatTreeNestedDataSource} from '@angular/material/tree';
import {NestedTreeControl} from '@angular/cdk/tree';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';

@Component({
  selector: 'app-gherkin',
  templateUrl: './gherkin.component.html',
  styleUrls: ['./gherkin.component.scss']
})
export class GherkinComponent implements OnInit, AfterViewChecked {

  gherkinData: Array<GherkinNode>;

  nestedTreeControl: NestedTreeControl<ExpandableNode>;
  nestedDataSource: MatTreeNestedDataSource<GherkinNode>;

  url: string;
  hash: string;

  @Output() gherkinDisplayed: EventEmitter<boolean> = new EventEmitter();
  gherkinDisplayedEmitted = false;


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

  updateGeneratedGherkin(gherkinData: Array<GherkinNode>) {
    this.gherkinDisplayedEmitted = false;
    this.gherkinData = gherkinData;
    this.nestedTreeControl = new NestedTreeControl<ExpandableNode>(this.getChildren);
    this.nestedDataSource = new MatTreeNestedDataSource();
    this.nestedDataSource.data = this.gherkinData;
    this.nestedTreeControl.dataNodes = this.gherkinData;
    this.nestedTreeControl.expandAll();

  }

  defineHash(hash: string) {
    this.hash = hash;
    this.selectHash();
  }


  selectHash() {
    if (this.hash) {
      const cmp = document.getElementById(this.hash);
      if (cmp) {
        cmp.scrollIntoView();
      }
      window.location.hash = this.hash;
    }
  }

  ngAfterViewChecked() {
    if (!this.gherkinDisplayedEmitted) {
      this.gherkinDisplayedEmitted = true;
      this.gherkinDisplayed.emit(true);
    }
  }


  hasNestedChild = (_: number, nodeData: GherkinNode) => nodeData.hasChilden();

  private getChildren = (node: ExpandableNode) => node.getChilden();


}
