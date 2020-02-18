import {AfterViewChecked, Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-gherkin-long-text',
  templateUrl: './gherkin-long-text.component.html',
  styleUrls: ['./gherkin-long-text.component.scss']
})
export class GherkinLongTextComponent implements OnInit, AfterViewChecked {

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

  ngAfterViewChecked(){
    if(document.getElementById('json')) {
      document.getElementById('json').innerHTML = JSON.stringify(this.json, undefined, 2);
    }
  }

}
