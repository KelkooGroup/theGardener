import {Component, Input, OnInit, Output} from '@angular/core';
import {DocumentationService} from "../../../_services/documentation.service";
import {DocumentationNode, DocumentationNodeApi} from "../../../_models/documentation";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-generate-documentation-output',
  templateUrl: './output.component.html',
  styleUrls: ['./output.component.css']
})
export class OutputComponent implements OnInit {

  @Input()
  display = false ;

  @Output()
  documentationData : DocumentationNode[]

  constructor(private documentationService: DocumentationService,  private route: ActivatedRoute) {

  }

  ngOnInit() {

    this.route.queryParams.subscribe(httpParams => {
      var httpParamsAsString = this.buildHttpParams(httpParams["node"],httpParams["project"] );
      this.generateDocumentation(httpParamsAsString);
    });
  }

  buildHttpParams(nodes: string[], projects: string[]   ) : string{
    var nodesArray = nodes;
    if (! (nodesArray instanceof Array)){
      nodesArray = new Array(nodesArray);
    }
    var projectsArray = projects ;
    if (! (projectsArray instanceof Array)){
      projectsArray = new Array(projectsArray);
    }
    var httpParams = "" ;
    for (var i = 0; i < nodesArray.length; i++) {
      if(nodesArray[i] != null) {
        httpParams += `node=${nodesArray[i]}&`;
      }
    }
    for (var i = 0; i < projectsArray.length; i++) {
      if(projectsArray[i] != null) {
        httpParams += `project=${projectsArray[i]}&`;
      }
    }
    return httpParams;
  }

  generateDocumentation(httpParams : string){
    this.documentationService.generateDocumentation(httpParams).subscribe(
      (result: DocumentationNodeApi) => {
        this.documentationData = this.documentationService.decorate(result) ;
      },
      err => {
      });
  }

}
