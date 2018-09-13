import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-generate-documentation-output',
  templateUrl: './output.component.html',
  styleUrls: ['./output.component.css']
})
export class OutputComponent implements OnInit {

  @Input()
  projects: string = "";

  constructor() { }

  ngOnInit() {
  }

}
