import {Component, Input, OnInit} from '@angular/core';
import {GherkinStep, GherkinTextFragment} from '../../../_models/hierarchy';

@Component({
  selector: 'app-gherkin-step',
  templateUrl: './gherkin-step.component.html',
  styleUrls: ['./gherkin-step.component.scss']
})
export class GherkinStepComponent implements OnInit {
  @Input() step: GherkinStep;
  table: Array<Array<string>>;
  longText: string;
  textFragments: Array<GherkinTextFragment>;

  constructor() {
  }

  ngOnInit(): void {
    if (this.step) {
      this.textFragments = this.initTextFragments(this.step.text);
      if (this.step.argument && this.step.argument.length > 0) {
        if (this.step.argument.length === 1) {
          this.longText = this.step.argument[0][0];
        } else {
          this.table = this.step.argument;
        }
      }
    }
  }

  private initTextFragments(stepText: string): Array<GherkinTextFragment> {
    const split = stepText.split(TEXT_WITH_ARGUMENTS_REGEX).filter(t => t);
    const match = stepText.match(TEXT_WITH_ARGUMENTS_REGEX);

    return split.map(f => {
      return {
        text: f,
        isParameter: match ? match.includes('"' + f + '"') : false,
      };
    });
  }
}

const TEXT_WITH_ARGUMENTS_REGEX = /"([^"]+)"/g;
