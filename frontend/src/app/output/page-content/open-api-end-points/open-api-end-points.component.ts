import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {OpenApiPath} from '../../../_models/open-api';
import {ConfigService} from '../../../_services/config.service';

declare const SwaggerUIBundle: any;

@Component({
  selector: 'app-open-api-end-points',
  templateUrl: './open-api-end-points.component.html',
  styleUrls: ['./open-api-end-points.component.scss']
})
export class OpenApiEndPointsComponent implements OnInit, AfterViewInit {

  @Input() openApiPathJson: OpenApiPath;
  @Input() position: number;

  id: string;
  proxyUrl: string;

  constructor(private applicationService: ConfigService) {
  }

  ngOnInit(): void {
    this.applicationService.getConfigs().subscribe(result => {
      this.proxyUrl = result.baseUrl + '/api/proxy';
    });
    this.id = 'swagger-ui-' + this.position;
  }

  ngAfterViewInit(): void {
    const jsonSpec = this.openApiPathJson.openApiSpec;
    const domId = '#' + this.id;
    SwaggerUIBundle({
      dom_id: domId,
      layout: 'BaseLayout',
      presets: [
        SwaggerUIBundle.presets.apis,
        SwaggerUIBundle.SwaggerUIStandalonePreset
      ],
      spec: jsonSpec,
      docExpansion: 'list',
      operationsSorter: 'alpha',
      defaultModelsExpandDepth: -1,
      enableCORS: false,
      showMutatedRequest: false,
      requestInterceptor: (request: any) => {
        request.url = this.proxyUrl + '?url=' + request.url.replace('http', this.openApiPathJson.protocol).replace(/&/g, 'amp') + '&body=' + request.body;
        return request
      }
    });
  }

  containError() {
    return this.openApiPathJson.errors.length !== 0 && this.openApiPathJson.errors[0] !== '';
  }
}

