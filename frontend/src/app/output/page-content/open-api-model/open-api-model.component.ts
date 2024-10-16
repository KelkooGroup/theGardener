import { Component, Input, OnInit } from '@angular/core';
import { OpenApiModel, OpenApiModelRow } from '../../../_models/open-api';

@Component({
  selector: 'app-open-api-model',
  templateUrl: './open-api-model.component.html',
  styleUrls: ['./open-api-model.component.scss']
})
export class OpenApiModelComponent implements OnInit {
  @Input() openApiModule: OpenApiModel;
  displayedColumns: Array<string> = ['title', 'type', 'description', 'example'];
  openApiRows: Array<OpenApiModelRow>;

  constructor() {}

  ngOnInit() {
    this.openApiRows = this.openApiModule.openApiRows;
  }

  isRequired(element: OpenApiModelRow) {
    return this.openApiModule.required && this.openApiModule.required.includes(element.title);
  }

  containError() {
    return this.openApiModule.errors.length !== 0 && this.openApiModule.errors[0] !== ' ';
  }
}
