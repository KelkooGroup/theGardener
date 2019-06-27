import {Component, Input, OnInit} from '@angular/core';
import {GherkinStepTable} from '../../../_models/gherkin';

@Component({
  selector: 'app-gherkin-table',
  templateUrl: './gherkin-table.component.html',
  styleUrls: ['./gherkin-table.component.scss']
})
export class GherkinTableComponent implements OnInit {

  @Input() table: GherkinStepTable;

  constructor() {
  }

  ngOnInit() {
  }

}
