import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {BranchApi, HierarchyNodeApi, ProjectApi} from "../../../../_models/criterias";
import {MatCheckboxChange, MatSelect} from "@angular/material";

@Component({
  selector: 'app-hierarchy-selector',
  templateUrl: './hierarchy-selector.component.html',
  styleUrls: ['./hierarchy-selector.component.css']
})
export class HierarchySelectorComponent implements OnInit {

  @Input()
  hierarchyLevel : HierarchyNodeApi ;
  showHierarchy : boolean ;
  selectedHierarchyLevel : HierarchyNodeApi ;
  previousSelectedHierarchyLevel : HierarchyNodeApi ;

  @Output() onSelectHierarchyLevel: EventEmitter<HierarchyNodeApi> = new EventEmitter();
  @Output() onDeselectHierarchyLevel: EventEmitter<HierarchyNodeApi> = new EventEmitter();
  @Output() onSelectProject:   EventEmitter<ProjectApi> = new EventEmitter();
  @Output() onDeselectProject: EventEmitter<ProjectApi> = new EventEmitter();
  @Output() onSelectBranch:   EventEmitter<BranchApi> = new EventEmitter();

  constructor() { }

  ngOnInit() {
    this.showHierarchy = this.hierarchyLevel.children != null && this.hierarchyLevel.children.length > 0 ;
  }

  selectHierarchyLevel(selectedHierarchyLevel : HierarchyNodeApi){
    this.onSelectHierarchyLevel.emit(selectedHierarchyLevel) ;
    if (this.previousSelectedHierarchyLevel != null){
      this.onDeselectHierarchyLevel.emit(this.previousSelectedHierarchyLevel) ;
    }
    this.previousSelectedHierarchyLevel = selectedHierarchyLevel;
  }

  selectProject(selectedProject : MatCheckboxChange){
    var projectId = selectedProject.source.value ;
    var project : ProjectApi;
    for (var j = 0; j < this.hierarchyLevel.projects.length; j++) {
      var loopProject = this.hierarchyLevel.projects[j];
      if(loopProject.id == projectId){
        project = loopProject;
      }
    }
    if (selectedProject.checked){
      this.onSelectProject.emit(project) ;
    }else{
      this.onDeselectProject.emit(project) ;
    }

  }

  selectBranch(selectedBranch : BranchApi){
    for (var j = 0; j < this.hierarchyLevel.projects.length; j++) {
      var loopProject = this.hierarchyLevel.projects[j];
      if(loopProject.id == selectedBranch.project.id){
        loopProject.selectedBranch = selectedBranch;
      }
    }

    this.onSelectBranch.emit(  selectedBranch) ;
  }

  propagateDeselectHierarchyLevel( childHierarchyDeselected: HierarchyNodeApi) : void {
    this.onDeselectHierarchyLevel.emit(childHierarchyDeselected) ;
  }

  propagateSelectHierarchyLevel( childHierarchySelected: HierarchyNodeApi) : void {
    this.onSelectHierarchyLevel.emit(childHierarchySelected) ;
  }

  propagateSelectProject( childProjectSelected: ProjectApi) : void {
    this.onSelectProject.emit(childProjectSelected) ;
  }

  propagateDeselectProject( childProjectDeselected: ProjectApi) : void {
    this.onDeselectProject.emit(childProjectDeselected) ;
  }

  propagateSelectBranch( childBranchSelected: BranchApi) : void {
    this.onSelectBranch.emit(childBranchSelected) ;
  }




}
