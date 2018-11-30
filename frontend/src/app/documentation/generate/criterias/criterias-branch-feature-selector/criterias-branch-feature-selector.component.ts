import {Component, Input, OnInit} from '@angular/core';
import {BranchSelector, FeatureSelector, ProjectSelector} from "../../../../_services/criteriasSelection";

@Component({
  selector: 'app-criterias-branch-feature-selector',
  templateUrl: './criterias-branch-feature-selector.component.html',
  styleUrls: ['./criterias-branch-feature-selector.component.scss']
})
export class CriteriasBranchFeatureSelectorComponent implements OnInit {

  @Input()
  project: ProjectSelector;

  constructor() { }

  ngOnInit() {
  }

  selectBranch(event) {
    const source = event.source.value as BranchSelector;
    source.selection();
  }

  selectFeature(event) {
    this.project.selectedBranch.selectFeature( event.source.value as FeatureSelector );
  }

}
