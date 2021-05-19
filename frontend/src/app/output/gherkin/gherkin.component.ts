import { Component, Input, OnInit } from '@angular/core';
import { Scenario } from '../../_models/gherkin';

@Component({
  selector: 'app-gherkin',
  templateUrl: './gherkin.component.html',
  styleUrls: ['./gherkin.component.scss']
})
export class GherkinComponent implements OnInit {
  @Input() scenarios: Scenario;

  constructor() {}

  ngOnInit(): void {}
}
