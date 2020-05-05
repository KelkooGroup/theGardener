import {Component, Input, OnInit} from '@angular/core';
import {MenuService} from '../../../_services/menu.service';
import {NotificationService} from '../../../_services/notification.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MenuHierarchy} from "../../../_models/menu";
import {NAVIGATE_PATH, RouteService} from "../../../_services/route.service";
import {NavigationParams, NavigationRoute} from "../../../_models/route";

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
                    const currentRoute = this.getCurrentRoute();

                    if ( !currentRoute.nodes || currentRoute.nodes?.length === 0 || currentRoute.page === undefined) {
                        this.navigateTo(this.items[0]);
                    }
                }
            }, error => {
                this.notificationService.showError('Error while getting first level of hierarchy', error);
            });
    }

    getCurrentRoute(): NavigationRoute{
        let params : NavigationParams;
        if (this.activatedRoute.firstChild && this.activatedRoute.firstChild.snapshot){
            params = this.activatedRoute.firstChild.snapshot.params;
        }
        if (!params && this.activatedRoute && this.activatedRoute.snapshot){
            params = this.activatedRoute.snapshot.params;
        }
        return this.routeService.navigationParamsToNavigationRoute(params);
    }

    navigateTo(item: MenuHierarchy) {
        const itemPath = this.routeService.menuHierarchyToFrontEndPath(item);
        this.router.navigateByUrl(NAVIGATE_PATH + itemPath.pathFromNodes);
    }

    isItemInActivatedRoute(item: MenuHierarchy) {
        return this.getCurrentRoute().nodes[0] === item.name;
    }

}
