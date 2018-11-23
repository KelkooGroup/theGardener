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

  @Input()
  display = false;

  @Output()
  documentationData: DocumentationNode[];

  showSpinner = true;

  @ViewChild(DocumentationThemeBookComponent)
  documentationTheme: DocumentationThemeBookComponent;

  constructor(private documentationService: DocumentationService, private route: ActivatedRoute) {

  }

  ngOnInit() {

    this.route.queryParams.subscribe(httpParams => {
      const httpParamsAsString = this.buildHttpParams(httpParams['node'], httpParams['project']);
      this.generateDocumentation(httpParamsAsString);
    });
  }

  buildHttpParams(nodes: string[], projects: string[]): string {
    let nodesArray = nodes;
    if (!(nodesArray instanceof Array)) {
      nodesArray = new Array(nodesArray);
    }
    let projectsArray = projects;
    if (!(projectsArray instanceof Array)) {
      projectsArray = new Array(projectsArray);
    }
    let httpParams = '';
    for (let i = 0; i < nodesArray.length; i++) {
      if (nodesArray[i] != null) {
        httpParams += `node=${nodesArray[i]}&`;
      }
    }
    for (let i = 0; i < projectsArray.length; i++) {
      if (projectsArray[i] != null) {
        httpParams += `project=${projectsArray[i]}&`;
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
