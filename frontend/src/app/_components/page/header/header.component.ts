import {Component, Input, OnInit} from '@angular/core';
import {MenuService} from '../../../_services/menu.service';
import {NotificationService} from '../../../_services/notification.service';
import {HierarchyNodeApi} from '../../../_models/hierarchy';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  @Input() logoSrc: string;

  @Input() appTitle?: string;

  items: Array<HierarchyNodeApi>;

  constructor(private menuService: MenuService,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.menuService.hierarchy().subscribe(
      result => {
        this.items = result && result.children;
      }, error => {
        this.notificationService.showError('Error while getting first level of hierarchy', error);
      });
  }

}
