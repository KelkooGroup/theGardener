import {
  AfterViewChecked,
  ChangeDetectorRef,
  Component,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';
import {BranchSelector, FeatureSelector, ProjectSelector} from '../../../../_services/criteriasSelection';
import {CriteriasFeatureSelectorComponent} from '../criterias-feature-selector/criterias-feature-selector.component';
import {MatSelect} from '@angular/material';

@Component({
  selector: 'app-criterias-branch-feature-selector',
  templateUrl: './criterias-branch-feature-selector.component.html',
  styleUrls: ['./criterias-branch-feature-selector.component.scss']
})
export class CriteriasBranchFeatureSelectorComponent implements OnInit, AfterViewChecked {

  @Input()
  project: ProjectSelector;

  @ViewChildren(CriteriasFeatureSelectorComponent)
  featureComponents: QueryList<CriteriasFeatureSelectorComponent>;


  constructor() {
  }

  ngOnInit() {
  }


  selectBranch(event) {
    const branch = event.source.value as BranchSelector;
    branch.selectBranch();

    // console.log(`Select  ${branch.name}`)
    this.updateFeaturesSelection(true);
  }

  selectFeature(feature: FeatureSelector) {
    this.project.selectedBranch.selectFeature(feature);
    this.featureComponents.forEach(f => f.filterBy(feature));
  }

  updateFeaturesSelection(selected: boolean) {
    if (selected) {
      this.project.selectedBranch.selectAllFeatures();
    } else {
      this.project.selectedBranch.selectNoneFeatures();
    }
    this.featureComponents.forEach(f => f.filterBy(this.project.selectedBranch.featureFilter));

  }

  ngAfterViewChecked() {
  }
}
