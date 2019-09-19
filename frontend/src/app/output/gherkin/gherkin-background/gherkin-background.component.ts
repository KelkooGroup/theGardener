import {Component, Input} from '@angular/core';
import {GherkinBackground} from '../../../_models/hierarchy';

@Component({
  selector: 'app-gherkin-background',
  templateUrl: './gherkin-background.component.html',
  styleUrls: ['./gherkin-background.component.scss']
})
export class GherkinBackgroundComponent {

  @Input() background: GherkinBackground;

  constructor() {
  }

}
