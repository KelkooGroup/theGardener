import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {CriteriasSelector, HierarchyNodeSelector} from "../../../_models/criteriasSelection";
import {HierarchyNodeApi} from "../../../_models/criterias";
import {HierarchyService} from "../../../_services/hierarchy.service";

@Component({
  selector: 'app-generate-documentation-criterias',
  templateUrl: './criterias.component.html',
  styleUrls: ['./criterias.component.css']
})
export class CriteriasComponent  {

  @Output()
  onGenerateDocumentationRequest: EventEmitter<string> = new EventEmitter();

  @Output()
  criteriasSelector = new CriteriasSelector() ;

  @Output()
  views : HierarchyNodeSelector[] ;

  @Output()
  view1 : HierarchyNodeSelector ;

  @Output()
  view2 : HierarchyNodeSelector ;


  constructor(private hierarchyService: HierarchyService) {
    this.hierarchyService.criterias().subscribe(
      (result: Array<HierarchyNodeApi>) => {
        this.criteriasSelector.hierarchyNodesSelector = HierarchyNodeSelector.buildHierarchyNodeSelectorAsTree(HierarchyNodeSelector.buildHierarchyNodeSelector(result)).children ;
        this.view1 = this.criteriasSelector.hierarchyNodesSelector[0] ;
        this.view2 = this.criteriasSelector.hierarchyNodesSelector[1] ;
        this.views = this.criteriasSelector.hierarchyNodesSelector ;

      },
      err => {
      });
  }

  generateDocumentation(){
    this.onGenerateDocumentationRequest.emit(this.criteriasSelector.buildHttpParams()) ;
  }

}
