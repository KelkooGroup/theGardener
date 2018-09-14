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
       this.displayCriteria(httpParams["projects"]);
    });
  }

  displayCriteria(projectsHttpParamValue: string   ){
    this.criteriaDisplay = this.criteriasSelector.humanizeHttpParams(projectsHttpParamValue);
  }

  generateDocumentation(){
    var httpParams = this.criteriasSelector.buildHttpParams() ;
    this.onGenerateDocumentationRequest.emit(httpParams) ;
    this.displayCriteria(httpParams.get("projects"));
    this.isCriteriaSelection = false ;
    this.isCriteriaDisplay = true ;
  }

}
