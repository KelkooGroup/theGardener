import {Component, Input} from '@angular/core';
import {Scenario} from '../../_models/hierarchy';

@Component({
  selector: 'app-gherkin',
  templateUrl: './gherkin.component.html',
  styleUrls: ['./gherkin.component.scss']
})
export class GherkinComponent {

  @Input() scenarios: Scenario;

  constructor() {
  }
}
