import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';
import {FeatureSelector} from "../../../../_services/criteriasSelection";
import {MatCheckbox} from "@angular/material";

@Component({
  selector: 'app-criterias-feature-selector',
  templateUrl: './criterias-feature-selector.component.html',
  styleUrls: ['./criterias-feature-selector.component.scss']
})
export class CriteriasFeatureSelectorComponent implements OnInit {

  @Output()
  featureSelected = new EventEmitter<FeatureSelector>();


  @Input()
  feature : FeatureSelector;

  @ViewChild(MatCheckbox)
  checkBox: MatCheckbox;

  selected : boolean = false;

  constructor() {
  }

  ngOnInit() {
  }

  selectFeature() {
    this.featureSelected.emit(this.feature);
  }

  filterBy(filter : FeatureSelector){
    if (filter.value == "*"){
      this.selected = true ;
    }else{
      this.selected = this.feature.value.includes( filter.value) ;
    }
  }

}
