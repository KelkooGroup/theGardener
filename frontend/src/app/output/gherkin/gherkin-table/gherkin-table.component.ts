import {Component, Input, OnInit} from '@angular/core';
import {GherkinExamples} from "../../../_models/gherkin";

@Component({
  selector: 'app-gherkin-table',
  templateUrl: './gherkin-table.component.html',
  styleUrls: ['./gherkin-table.component.scss']
})
export class GherkinTableComponent implements OnInit {

  @Input() table: Array<Array<string>>;
  @Input() examples: Array<GherkinExamples>;

  tableHeader: Array<string>;
  tableRows: Array<object> = [];

  constructor() {
  }

  ngOnInit() {
    if ( this.table ){
      this.tableHeader = this.table[0];
      this.tableRows = this.table.slice(1);
    }
    if ( this.examples && this.examples.length >0  ){
      this.tableHeader = this.examples[0].tableHeader;
      this.tableRows = this.examples[0].tableBody;
    }
  }
}
