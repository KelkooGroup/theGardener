import {Component, Input, OnInit} from '@angular/core';
import {MenuService} from '../../../_services/menu.service';
import {NotificationService} from '../../../_services/notification.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MenuHierarchy} from "../../../_models/menu";
import {NAVIGATE_PATH, RouteService} from "../../../_services/route.service";
import {NavigationParams} from "../../../_models/route";
import {MobileMenuHelperService} from '../../../_services/mobile-menu-helper.service';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
    @Input() logoSrc: string;

    @Input() appTitle?: string;

    items: Array<MenuHierarchy>;
    params: NavigationParams;
    url: string;

    constructor(private menuService: MenuService,
                private routeService: RouteService,
                private notificationService: NotificationService,
                private activatedRoute: ActivatedRoute,
                private router: Router,
                public mobileMenuHelper: MobileMenuHelperService) {
    }

    ngOnInit() {
        this.initAttributes();
        this.menuService.getMenuHeader().subscribe(
            result => {
                this.items = result;
                if (this.items.length > 0) {
                    const currentRoute = this.routeService.navigationParamsToNavigationRoute(this.params);
                    if ( this.url== undefined || this.routeService.isNavigationUrl(this.url)) {
                        if (!currentRoute.nodes || currentRoute.nodes?.length === 0 || currentRoute.page === undefined) {
                            this.navigateTo(this.items[0]);
                        }
                    }
                }
            }, error => {
                this.notificationService.showError('Error while getting first level of hierarchy', error);
            });
    }

    initAttributes() {
        if (this.activatedRoute.firstChild && this.activatedRoute.firstChild.snapshot) {
            this.params = this.activatedRoute.firstChild.snapshot.params;
            this.url = this.activatedRoute.firstChild.snapshot.url?.join('/');
        }
        if (!this.params && this.activatedRoute && this.activatedRoute.snapshot) {
            this.params = this.activatedRoute.snapshot.params;
            this.url = this.activatedRoute.snapshot.url?.join('/');
        }
    }

    navigateTo(item: MenuHierarchy) {
        const itemPath = this.routeService.menuHierarchyToFrontEndPath(item);
        this.router.navigateByUrl(NAVIGATE_PATH + itemPath.pathFromNodes);
    }

    isItemInActivatedRoute(item: MenuHierarchy) {
        return this.url && this.routeService.isNavigationUrl(this.url) && this.routeService.navigationParamsToNavigationRoute(this.params).nodes[0] === item.name;
    }
}
