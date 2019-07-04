import {Component, Input, OnInit} from '@angular/core';
import {NavigationItem} from "../../../_models/navigation";
import {HierarchyNodeApi} from "../../../_models/hierarchy";
import {MenuService} from "../../../_services/menu.service";
import {NotificationService} from "../../../_services/notification.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  @Input() logoSrc: string;

  @Input() appTitle?: string;

  items: Array<NavigationItem>;

  constructor(private menuService: MenuService,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.menuService.hierarchy().subscribe(
      (result: HierarchyNodeApi) => {
        const hierarchyNodeSelectorTree = this.menuService.buildHierarchyNodeSelector(result);
        this.items = hierarchyNodeSelectorTree && hierarchyNodeSelectorTree.children;
      }, error => {
        this.notificationService.showError('Error while getting first level of hierarchy', error);
      });
  }

}
