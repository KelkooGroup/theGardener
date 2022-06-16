import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MenuService } from '../../../_services/menu.service';
import { catchError, map, switchMap } from 'rxjs/operators';
import { MenuHierarchy } from '../../../_models/menu';
import { EMPTY, Subscription } from 'rxjs';
import { NotificationService } from '../../../_services/notification.service';

@Component({
  selector: 'app-navigate-menu',
  templateUrl: './navigate-menu.component.html',
  styleUrls: ['./navigate-menu.component.scss']
})
export class NavigateMenuComponent implements OnInit, OnDestroy {
  hierarchy: Array<MenuHierarchy>;
  depth: number;
  expanded: boolean;
  private subscription: Subscription;

  constructor(private activatedRoute: ActivatedRoute, private menuService: MenuService, private notificationService: NotificationService) {}

  ngOnInit() {
    this.subscription = this.activatedRoute.params
      .pipe(
        map(params => params['nodes']),
        switchMap((nodeNames: string) => this.menuService.getSubMenuForNode(nodeNames)),
        catchError(error => {
          this.notificationService.showError(`Unable to load navigation hierarchy`, error);
          return EMPTY;
        })
      )
      .subscribe(res => {
        this.hierarchy = res;
      });
  }

  trackItem(_index: number, item: MenuHierarchy) {
    return item.name;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
