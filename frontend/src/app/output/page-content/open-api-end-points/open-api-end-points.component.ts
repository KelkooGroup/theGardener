import {Component, Input, OnInit} from '@angular/core';
import {OpenApiPath} from "../../../_models/open-api";

declare const SwaggerUIBundle: any;

@Component({
  selector: 'app-open-api-end-points',
  templateUrl: './open-api-end-points.component.html',
  styleUrls: ['./open-api-end-points.component.css']
})
export class OpenApiEndPointsComponent implements OnInit {

  @Input() openApiPathJson: OpenApiPath;

  constructor() { }

  ngOnInit(): void {
    SwaggerUIBundle({
      dom_id: '#swagger-ui',
      layout: 'BaseLayout',
      presets: [
        SwaggerUIBundle.presets.apis,
        SwaggerUIBundle.SwaggerUIStandalonePreset
      ],
      spec: this.openApiPathJson.OpenApiSpec,
      docExpansion: 'list',
      operationsSorter: 'alpha',
      defaultModelsExpandDepth: -1,
      enableCORS: false
    });
  }
}
