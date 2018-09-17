import {Component, Input, OnInit, Output} from '@angular/core';
import {HttpParams} from "@angular/common/http";
import {DocumentationService} from "../../../_services/documentation.service";
import {HierarchyNodeApi} from "../../../_models/criterias";
import {HierarchyNodeSelector} from "../../../_models/criteriasSelection";
import {DocumentationNodeApi} from "../../../_models/documentation";

@Component({
  selector: 'app-generate-documentation-output',
  templateUrl: './output.component.html',
  styleUrls: ['./output.component.css']
})
export class OutputComponent implements OnInit {

  @Input()
  display = false ;

  @Output()
  result : DocumentationNodeApi[]

  constructor(private documentationService: DocumentationService) {

  }

  ngOnInit() {
  }

  generateDocumentation(httpParams : string){
    this.documentationService.generateDocumentation(httpParams).subscribe(
      (result: Array<DocumentationNodeApi>) => {
        this.result = result ;
      },
      err => {
      });
  }

}
