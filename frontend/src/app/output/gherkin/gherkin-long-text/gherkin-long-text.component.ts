import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-gherkin-long-text',
  templateUrl: './gherkin-long-text.component.html',
  styleUrls: ['./gherkin-long-text.component.scss']
})
export class GherkinLongTextComponent implements OnInit {

  @Input() longText: string;

  isJson: boolean;
  json: any;

  constructor() {
  }

  ngOnInit() {
    this.jsonText = this.longText;
  }

  set jsonText(text: string) {
    try {
      this.json = JSON.parse(text);
      this.isJson = true;
    } catch (e) {
      this.isJson = false;
    }
  }

}
