import {Component, Input, OnInit} from '@angular/core';
import {NestedTreeControl} from '@angular/cdk/tree';
import {ExpandableNode, GherkinNode} from '../../../_models/gherkin';

@Component({
  selector: 'app-gherkin-feature',
  templateUrl: './gherkin-feature.component.html',
  styleUrls: ['./gherkin-feature.component.scss']
})
export class GherkinFeatureComponent implements OnInit {

  @Input() currentNode: GherkinNode;
  @Input() url: string;
  @Input() nestedTreeControl: NestedTreeControl<ExpandableNode>;

  constructor() {
  }

  ngOnInit() {
  }

}
