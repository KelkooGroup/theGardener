import {Component, Input, OnInit} from '@angular/core';
import {MenuService} from '../../../_services/menu.service';
import {NotificationService} from '../../../_services/notification.service';
import {HierarchyNodeApi} from '../../../_models/hierarchy';
import {ActivatedRoute, Router} from '@angular/router';
import {UrlCleanerService} from '../../../_services/url-cleaner.service';

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
              private urlCleanerService: UrlCleanerService,
              private notificationService: NotificationService,
              private activatedRoute: ActivatedRoute,
              private router: Router) {
  }

  ngOnInit() {
    this.menuService.getMenuHeader().subscribe(
      result => {
        this.items = result && result.children;
        if (!this.activatedRoute.firstChild || !this.activatedRoute.firstChild.snapshot.params.name) {
          if (this.items.length > 0) {
            this.navigateTo(this.items[0]);
          }
        }
      }, error => {
        this.notificationService.showError('Error while getting first level of hierarchy', error);
      });
  }

  navigateTo(node: HierarchyNodeApi) {
    if (node.directory) {
      this.router.navigate(['app/documentation/navigate/', node.hierarchy, {path: this.urlCleanerService.relativePathToUrl(node.directory.path)}]);
    } else {
      this.router.navigate(['app/documentation/navigate/', node.hierarchy]);
    }
  }

  isItemInActivatedRoute(item: HierarchyNodeApi) {
    const url = this.activatedRoute.firstChild.snapshot.url;
    return url.filter(p => p.path === item.hierarchy).length > 0;
  }

}
