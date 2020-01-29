import {Component, Input, OnInit} from '@angular/core';
import {OpenApiModel, OpenApiModelRow} from "../../../_models/openApi";

@Component({
  selector: 'app-open-api-model',
  templateUrl: './open-api-model.component.html',
  styleUrls: ['./open-api-model.component.scss']
})
export class OpenApiModelComponent implements OnInit {

  @Input() openApiModule: OpenApiModel;
  displayedColumns: string[] = ['title', 'type', 'default', 'description', 'example'];
  openApiRows: Array<OpenApiModelRow>;

  constructor() { }

  ngOnInit() {
    this.openApiRows = this.openApiModule.openApiRows;
  }

}
