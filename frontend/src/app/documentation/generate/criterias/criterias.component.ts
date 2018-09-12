import {Component, OnInit, Input} from '@angular/core';
import {HierarchyService} from "../../../_services/hierarchy.service";
import {BranchApi, CriteriasV1, HierarchyNodeApi, ProjectApi} from "../../../_models/criterias";

@Component({
  selector: 'app-generate-documentation-criterias',
  templateUrl: './criterias.component.html',
  styleUrls: ['./criterias.component.css']
})
export class CriteriasComponent implements OnInit {

  hierarchyLevel0 : HierarchyNodeApi ;
  criterias = new CriteriasV1();
  httpParams : string ;

  constructor(private hierarchyService: HierarchyService){

  }

  ngOnInit() {
    this.hierarchyService.criterias().subscribe(
      (result: Array<HierarchyNodeApi>) => {
        HierarchyNodeApi.setCrossLinks(result) ;
        this.hierarchyLevel0 = HierarchyNodeApi.buildTree(result) ;
      },
      err => {

      });

  }

  selectHierarchyNode(selectedNode : HierarchyNodeApi) : void{
    this.criterias.includeHierarchyNode(selectedNode) ;
    this.httpParams = this.criterias.httpParams() ;
  }

  deselectHierarchyNode(deselectedNode : HierarchyNodeApi) : void{
    this.criterias.excludeHierarchyNode(deselectedNode) ;
    this.httpParams = this.criterias.httpParams() ;
  }

  selectProject(project : ProjectApi) : void{
    this.criterias.includeProject(project) ;
    this.httpParams = this.criterias.httpParams() ;
  }

  deselectProject(project : ProjectApi) : void{
    this.criterias.excludeProject(project) ;
    this.httpParams = this.criterias.httpParams() ;
  }

  selectBranch(branch : BranchApi) : void{
    this.criterias.changeBranch(branch) ;
    this.httpParams = this.criterias.httpParams() ;
  }

}
