import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {CriteriasSelector, HierarchyNodeSelector} from "../../../_models/criteriasSelection";

@Component({
  selector: 'app-generate-documentation-criterias',
  templateUrl: './criterias.component.html',
  styleUrls: ['./criterias.component.css']
})
export class CriteriasComponent  {

  @Output()
  onGenerateDocumentationRequest: EventEmitter<string> = new EventEmitter();

  criteriasSelector = new CriteriasSelector() ;

  storeHierarchyNodesSelector(hierarchyNodesSelector : HierarchyNodeSelector[]){
    this.criteriasSelector.hierarchyNodesSelector = hierarchyNodesSelector ;
  }
  generateDocumentation(){
    this.onGenerateDocumentationRequest.emit(this.criteriasSelector.buildHttpParams()) ;
  }

}
