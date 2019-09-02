import {Component, Input, OnInit} from '@angular/core';
import {MenuService} from '../../../_services/menu.service';
import {NotificationService} from '../../../_services/notification.service';
import {HierarchyNodeApi} from '../../../_models/hierarchy';
import {ActivatedRoute, Router} from '@angular/router';

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
              private notificationService: NotificationService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit() {
    this.menuService.getMenuHeader().subscribe(
      result => {
        this.items = result && result.children;
        if (!this.activatedRoute.snapshot.params || !this.activatedRoute.snapshot.params.name) {
          if (this.items.length > 0) {
            this.router.navigate(['app/documentation/navigate/' , this.items[0].hierarchy]);
          }
        }
      }, error => {
        this.notificationService.showError('Error while getting first level of hierarchy', error);
      });
  }

}
