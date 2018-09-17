import {Component, Input, Output, EventEmitter} from '@angular/core';
import {CriteriasDisplay, CriteriasSelector, HierarchyNodeSelector} from "../../../_models/criteriasSelection";
import {HierarchyNodeApi} from "../../../_models/criterias";
import {HierarchyService} from "../../../_services/hierarchy.service";
import {ActivatedRoute, Params} from "@angular/router";
import {HttpParams} from "@angular/common/http";

@Component({
  selector: 'app-generate-documentation-criterias',
  templateUrl: './criterias.component.html',
  styleUrls: ['./criterias.component.css']
})
export class CriteriasComponent  {

  @Input()
  isCriteriaSelection : boolean = false;

  @Input()
  isCriteriaDisplay   : boolean = false;

  @Output()
  onGenerateDocumentationRequest: EventEmitter<HttpParams> = new EventEmitter();

  @Output()
  criteriasSelector = new CriteriasSelector() ;

  @Output()
  views : HierarchyNodeSelector[] ;

  @Output()
  criteriaDisplay: CriteriasDisplay ;

  constructor(private hierarchyService: HierarchyService, private route: ActivatedRoute) {
    this.hierarchyService.criterias().subscribe(
      (result: Array<HierarchyNodeApi>) => {
        this.criteriasSelector.hierarchyNodesSelector = HierarchyNodeSelector.buildHierarchyNodeSelectorAsTree(HierarchyNodeSelector.buildHierarchyNodeSelector(result)).children ;
        this.views = this.criteriasSelector.hierarchyNodesSelector ;

      },
      err => {
      });

    this.route.queryParams.subscribe(httpParams => {
       this.displayCriteria(httpParams["node"],httpParams["project"]);
    });
  }

  displayCriteria(nodes: string[], projects: string[]   ){
    var nodesArray = nodes;
    if (! (nodesArray instanceof Array)){
      nodesArray = new Array(nodesArray);
    }
    var projectsArray = projects ;
    if (! (projectsArray instanceof Array)){
      projectsArray = new Array(projectsArray);
    }

    this.criteriaDisplay = this.criteriasSelector.humanizeHttpParams(nodesArray,projectsArray);
    this.isCriteriaSelection = false ;
    this.isCriteriaDisplay = true ;
  }

  generateDocumentation(){
    var httpParams = this.criteriasSelector.buildHttpParams() ;
    this.onGenerateDocumentationRequest.emit(httpParams) ;
    this.displayCriteria(httpParams.getAll("node"),httpParams.getAll("project"));
  }

}
