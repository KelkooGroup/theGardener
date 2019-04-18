import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CriteriasDisplay, CriteriasSelector, HierarchyNodeSelector, } from '../../../_services/criterias-selection';
import {HierarchyNodeApi} from '../../../_models/criterias';
import {CriteriasService} from '../../../_services/criterias.service';
import {ActivatedRoute} from '@angular/router';
import {HttpParams} from '@angular/common/http';

@Component({
  selector: 'app-generate-documentation-criterias',
  templateUrl: './criterias.component.html',
  styleUrls: ['./criterias.component.scss']
})
export class CriteriasComponent {

  @Input() isCriteriaSelection = false;

  @Input() isCriteriaDisplay = false;

  @Output() readonly generateDocumentationRequestEventEmitter: EventEmitter<HttpParams> = new EventEmitter();

  @Output() readonly criteriasSelector = new CriteriasSelector();

  @Output() views: Array<HierarchyNodeSelector>;

  @Output() criteriaDisplay: CriteriasDisplay;

  constructor(private criteriasService: CriteriasService, private route: ActivatedRoute) {
    this.criteriasService.criterias().subscribe(
      (result: Array<HierarchyNodeApi>) => {
        const hierarchyNodeSelectorTree = criteriasService.buildHierarchyNodeSelectorAsTree(criteriasService.buildHierarchyNodeSelector(result));
        this.criteriasSelector.hierarchyNodesSelector = hierarchyNodeSelectorTree && hierarchyNodeSelectorTree.children;
        this.views = this.criteriasSelector.hierarchyNodesSelector;

      },
      () => {
      });

    this.route.queryParams.subscribe(httpParams => {
      this.displayCriteria(httpParams.node, httpParams.project);
    });
  }

  displayCriteria(nodes: Array<string>, projects: Array<string>) {
    let nodesArray = nodes;
    if (!(nodesArray instanceof Array)) {
      nodesArray = new Array(nodesArray);
    }
    let projectsArray = projects;
    if (!(projectsArray instanceof Array)) {
      projectsArray = new Array(projectsArray);
    }

    this.criteriaDisplay = this.criteriasSelector.humanizeHttpParams(nodesArray, projectsArray);
    this.isCriteriaSelection = false;
    this.isCriteriaDisplay = true;
  }

  generateDocumentation() {
    const httpParams = this.criteriasSelector.buildHttpParams();
    this.generateDocumentationRequestEventEmitter.emit(httpParams);
    this.displayCriteria(httpParams.getAll('node'), httpParams.getAll('project'));
  }

}


