import {Component, Input, OnInit} from '@angular/core';
import {GherkinStep} from '../../../_models/gherkin';

@Component({
  selector: 'app-gherkin-step',
  templateUrl: './gherkin-step.component.html',
  styleUrls: ['./gherkin-step.component.scss']
})
export class GherkinStepComponent implements OnInit {

  @Input() step: GherkinStep;

  constructor() {
  }

  ngOnInit() {
  }

}
