import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output, QueryList, ViewChildren} from '@angular/core';
import {
  BranchSelector,
  DirectorySelector,
  FeatureSelector,
  HierarchyNodeSelector,
  ProjectSelector,
  SelectionEvent
} from "../../../_services/criterias-selection";
import {MatExpansionPanel} from "@angular/material";

@Component({
  selector: 'app-navigate-tree',
  templateUrl: './navigate-tree.component.html',
  styleUrls: ['./navigate-tree.component.scss']
})
export class NavigateTreeComponent implements OnInit, AfterViewInit {

  loading = true ;
  open = false ;

  @Input()
  parentPath: string;

  @Input()
  level: number;

  @Input()
  nodes: HierarchyNodeSelector[];

  @Input()
  projects: ProjectSelector[];

  @Input()
  directories: DirectorySelector[];

  @Input()
  features: FeatureSelector[];

  @ViewChildren(MatExpansionPanel)
  expansions : QueryList<MatExpansionPanel>;

  @ViewChildren(NavigateTreeComponent)
  trees : QueryList<NavigateTreeComponent>;

  selectedPath = "";

  selectedBranch :  BranchSelector;

  constructor() {

  }

  navigateTo(hash: string){
    if ( hash.startsWith(this.parentPath)){
      console.log(`Opening ${this.parentPath}`);
      //this.expansions.forEach( e => e.open());
      this.trees.forEach( e => e.navigateTo(hash));
    }
  }

  setSelectedNode(node: HierarchyNodeSelector) {
    this.setSelectedPath(node.id);
    this.selection.emit(new SelectionEvent(null, null, null, null, node));
  }

  setSelectedProject(project: ProjectSelector) {
    this.setSelectedPath(project.id);
    this.setSelectedBranch(project.stableBranch);
    this.selection.emit(new SelectionEvent(null, null, null, project, null));
  }

  setSelectedBranch(branch: BranchSelector) {
    this.selectedBranch = branch;
    this.setSelectedPath(branch.name);
    this.selection.emit(new SelectionEvent(null, null, branch, null, null));
  }

  setSelectedDirectory(directory: DirectorySelector) {
    this.setSelectedPath(directory.name);
    this.selection.emit(new SelectionEvent(null, directory, null, null, null));
  }

  setSelectedFeature(feature: FeatureSelector) {
    this.setSelectedPath(feature.value);
    this.selection.emit(new SelectionEvent(feature, null, null, null, null));
  }

  setSelectedPath(path: string) {
    this.selectedPath = path;
  }

  selectionFromChild(selection : SelectionEvent){
    this.selection.emit(selection);
  }

  @Output()
  selection: EventEmitter<SelectionEvent> = new EventEmitter();

  @Output()
  treeLoaded: EventEmitter<Boolean> = new EventEmitter();



  ngOnInit() {
    this.loading = false;
  }

  ngAfterViewInit() {
    this.treeLoaded.emit(true);
  }

}
