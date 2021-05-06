import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {MenuHierarchy} from '../../../_models/menu';
import {MenuService} from '../../../_services/menu.service';
import {NAVIGATE_PATH, RouteService} from '../../../_services/route.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-navigate-mobile-menu',
  templateUrl: './navigate-mobile-menu.component.html',
  styleUrls: ['./navigate-mobile-menu.component.scss']
})
export class NavigateMobileMenuComponent implements OnInit {

  @Output() navigationEvent = new EventEmitter<string>();

  fullMenuForMobile: Array<MenuHierarchy>;

  expandedItem: MenuHierarchy;

  constructor(private menuService: MenuService,
              private routeService: RouteService,
              private router: Router) { }

  ngOnInit(): void {
    this.menuService.getMenu()
      .subscribe(menu => {
        this.fullMenuForMobile = menu;
      });
  }

  expand(item: MenuHierarchy) {
    const itemPath = this.routeService.menuHierarchyToFrontEndPath(item);
    this.router.navigateByUrl(NAVIGATE_PATH + itemPath.pathFromNodes);
    this.expandedItem = item;
  }

  trackItem(index: number, item: MenuHierarchy) {
    return item.name;
  }

  onNavigationEvent(url: string) {
    this.navigationEvent.emit(url);
  }
}
