import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-gherkin-long-text',
  templateUrl: './gherkin-long-text.component.html',
  styleUrls: ['./gherkin-long-text.component.scss']
})
export class GherkinLongTextComponent implements OnInit {

  @Input() longText: string;

  isJson: boolean;
  json: object;

  constructor() {
  }

  ngOnInit() {
    try {
      this.json = JSON.parse(this.longText);
      this.isJson = true;
    } catch (e) {
      this.isJson = false;
    }
  }

}
