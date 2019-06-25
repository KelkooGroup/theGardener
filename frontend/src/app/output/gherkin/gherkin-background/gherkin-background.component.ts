import {Component, Input, OnInit} from '@angular/core';
import {GherkinStep} from '../../../_models/gherkin';

@Component({
  selector: 'app-gherkin-background',
  templateUrl: './gherkin-background.component.html',
  styleUrls: ['./gherkin-background.component.scss']
})
export class GherkinBackgroundComponent implements OnInit {

  @Input() steps: Array<GherkinStep>;

  constructor() {
  }

  ngOnInit() {
  }

}
