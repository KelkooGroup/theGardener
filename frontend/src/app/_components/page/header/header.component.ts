import {Component, Input, OnInit} from '@angular/core';
import {MenuService} from '../../../_services/menu.service';
import {NotificationService} from '../../../_services/notification.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MenuHierarchy} from "../../../_models/menu";
import {NAVIGATE_PATH, RouteService} from "../../../_services/route.service";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
    @Input() logoSrc: string;

    @Input() appTitle?: string;

    items: Array<MenuHierarchy>;

    constructor(private menuService: MenuService,
                private routeService: RouteService,
                private notificationService: NotificationService,
                private activatedRoute: ActivatedRoute,
                private router: Router) {
    }

    ngOnInit() {
        this.menuService.getMenuHeader().subscribe(
            result => {
                this.items = result;
                if (this.items.length > 0) {
                    const currentRoute = this.routeService.navigationParamsToNavigationRoute(this.activatedRoute.firstChild.snapshot.params);
                    if ( currentRoute.nodes == undefined || currentRoute.nodes?.length == 0 ) {
                        this.navigateTo(this.items[0]);
                    }
                }
            }, error => {
                this.notificationService.showError('Error while getting first level of hierarchy', error);
            });
    }

    navigateTo(item: MenuHierarchy) {
        const itemPath = this.routeService.menuHierarchyToFrontEndPath(item);
        this.router.navigateByUrl(NAVIGATE_PATH + itemPath.pathFromNodes);
    }

    isItemInActivatedRoute(item: MenuHierarchy) {
        const currentRoute = this.routeService.navigationParamsToNavigationRoute(this.activatedRoute.firstChild.snapshot.params);
        return currentRoute.nodes[0] == item.name;
    }

}
