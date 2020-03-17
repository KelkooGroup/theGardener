import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {OpenApiPath} from '../../../_models/open-api';

declare const SwaggerUIBundle: any;

@Component({
  selector: 'app-open-api-end-points',
  templateUrl: './open-api-end-points.component.html',
  styleUrls: ['./open-api-end-points.component.css']
})
export class OpenApiEndPointsComponent implements OnInit,AfterViewInit {

  @Input() openApiPathJson: OpenApiPath;
  @Input() position: number;

   id:string;

  constructor() { }

  ngOnInit(): void {
    this.id  = 'swagger-ui-' + this.position;

  }

  ngAfterViewInit(): void {
    const jsonSpec = this.openApiPathJson.openApiSpec;
    const dom_id = '#' + this.id;
    SwaggerUIBundle({
      dom_id: dom_id,
      layout: 'BaseLayout',
      presets: [
        SwaggerUIBundle.presets.apis,
        SwaggerUIBundle.SwaggerUIStandalonePreset
      ],
      spec: jsonSpec,
      docExpansion: 'list',
      operationsSorter: 'alpha',
      defaultModelsExpandDepth: -1,
      enableCORS: false
    });
  }
}
