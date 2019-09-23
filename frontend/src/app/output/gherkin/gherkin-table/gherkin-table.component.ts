import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-gherkin-table',
  templateUrl: './gherkin-table.component.html',
  styleUrls: ['./gherkin-table.component.scss']
})
export class GherkinTableComponent implements OnInit {

  @Input() table: Array<Array<string>>;
  tableHeader: Array<string>;
  tableRows: Array<object> = [];

  constructor() {
  }

  ngOnInit() {
    this.tableHeader = this.table[0];
    this.tableRows = this.table.slice(1);
  }
}
