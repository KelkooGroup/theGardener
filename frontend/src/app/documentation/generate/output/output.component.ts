import {Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {DocumentationService} from '../../../_services/documentation.service';
import {DocumentationNode, DocumentationNodeApi} from '../../../_models/documentation';
import {ActivatedRoute} from '@angular/router';
import {DocumentationThemeBookComponent} from './themes/documentation-theme-book/documentation-theme-book.component';

@Component({
  selector: 'app-generate-documentation-output',
  templateUrl: './output.component.html',
  styleUrls: ['./output.component.css']
})
export class OutputComponent implements OnInit {

  @Input() display = false;

  @Output() documentationData: Array<DocumentationNode>;

  showSpinner = true;

  @ViewChild(DocumentationThemeBookComponent) documentationTheme: DocumentationThemeBookComponent;

  constructor(private documentationService: DocumentationService, private route: ActivatedRoute) {

  }

  ngOnInit() {

    this.route.queryParams.subscribe(httpParams => {
      const httpParamsAsString = this.buildHttpParams(httpParams.node, httpParams.project);
      this.generateDocumentation(httpParamsAsString);
    });
  }

  buildHttpParams(nodes: Array<string>, projects: Array<string>): string {
    let nodesArray = nodes;
    if (!(nodesArray instanceof Array)) {
      nodesArray = new Array(nodesArray);
    }
    let projectsArray = projects;
    if (!(projectsArray instanceof Array)) {
      projectsArray = new Array(projectsArray);
    }
    let httpParams = '';
    for (const node of nodesArray) {
      if (node != null) {
        httpParams += `node=${node}&`;
      }
    }
    for (const project of projectsArray) {
      if (project) {
        httpParams += `project=${project}&`;
      }
    }
    return httpParams;
  }

  generateDocumentation(httpParams: string) {
    if (httpParams !== '') {
      this.showSpinner = true;
      this.documentationService.generateDocumentation(httpParams).subscribe(
        (result: DocumentationNodeApi) => {
          this.documentationData = this.documentationService.decorate(result);
          if (this.documentationTheme) {
            this.documentationTheme.updateGeneratedDocumentation(this.documentationData);
          }
          this.showSpinner = false;
          new Promise(() => {
            setTimeout(() => {
              if (this.documentationTheme) {
                this.documentationTheme.selectHash();
              }
            }, 1000);
          });
        },
        () => {
        });
    }
  }

}
