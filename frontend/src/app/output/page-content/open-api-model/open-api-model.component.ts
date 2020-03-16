import {Component, Input, OnInit} from '@angular/core';
import {OpenApiModel, OpenApiModelRow} from '../../../_models/open-api';
import {NotificationService} from '../../../_services/notification.service';

@Component({
  selector: 'app-open-api-model',
  templateUrl: './open-api-model.component.html',
  styleUrls: ['./open-api-model.component.scss']
})
export class OpenApiModelComponent implements OnInit {

  @Input() openApiModule: OpenApiModel;
  displayedColumns: Array<string> = ['title', 'type', 'description', 'example'];
  openApiRows: Array<OpenApiModelRow>;

  constructor(private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.openApiRows = this.openApiModule.openApiRows;
    if (this.openApiModule.errors.length > 0) {
      this.notificationService.showError(`Error while loading Model`, this.openApiModule.errors[0]);
    }
  }

  isRequired(element: OpenApiModelRow) {
    return this.openApiModule.required.includes(element.title);
  }

}
