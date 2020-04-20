import {
    Component,
    Input,
    OnDestroy,
    OnInit,
} from '@angular/core';
import {MenuHierarchy, MenuProjectHierarchy} from '../../../../_models/menu';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {NotificationService} from '../../../../_services/notification.service';
import {NAVIGATE_PATH, RouteService} from "../../../../_services/route.service";
import {FrontendPath, NavigationRoute} from "../../../../_models/route";


@Component({
    selector: 'app-navigate-menu-item',
    templateUrl: './navigate-menu-item.component.html',
    styleUrls: ['./navigate-menu-item.component.scss'],
})
export class NavigateMenuItemComponent implements OnInit, OnDestroy {

    @Input() menuItem: MenuHierarchy;

    currentPath: FrontendPath;
    currentNavigationRoute: NavigationRoute;

    paddingByDepth = 16.75;
    expanded: boolean;
    active: boolean;
    disable: boolean;
    leafSelection: boolean;
    selectedBranch: MenuHierarchy;

    private subscription: Subscription;

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private routeService: RouteService,
                private notificationService: NotificationService) {
    }

    ngOnInit() {
        this.subscription = this.activatedRoute.params
            .subscribe(params => {

                this.currentPath = this.routeService.navigationParamsToFrontEndPath(params);
                this.currentNavigationRoute = this.routeService.navigationParamsToNavigationRoute(params);
                this.active = this.isActive(this.menuItem);
                this.expanded =  this.menuItem.depth == 0 ||  this.active;

                if (this.menuItem.type === 'Project') {
                    const project = this.menuItem as MenuProjectHierarchy;
                    const branchToSelect = this.routeService.selectBranchFromNavigationRoute(this.currentNavigationRoute, project.stableBranch);

                    this.active = this.currentNavigationRoute.project == project.name;
                    if (this.active) {
                        this.selectedBranch = project.children.find(b => b.name === branchToSelect);
                        this.expanded = true;
                    } else {
                        this.selectedBranch = project.children.find(b => b.name === project.stableBranch);
                    }
                }

            }, error => {
                this.notificationService.showError(`Error while showing menu item ${this.menuItem.name}`, error);
            });


    }

    navigateToItem() {
        if (this.menuItem.route?.page !== undefined) {
            this.router.navigateByUrl(NAVIGATE_PATH + this.routeService.navigationRouteToFrontEndPath(this.menuItem.route).pathFromNodes);
        } else {
            this.expanded = !this.expanded;
        }
    }

    navigateToSelectedBranch() {
        const branchPath = this.routeService.navigationRouteToFrontEndPath(this.selectedBranch.route).pathFromNodes;
        this.router.navigateByUrl(NAVIGATE_PATH + branchPath);
    }

    isActive(menuItem: MenuHierarchy): boolean {

        const menuItemNodesPath = this.routeService.navigationRouteToFrontEndPath(menuItem.route).nodesPath;
        let active = false ;
        if (menuItem.type == "Node") {
            active = this.currentPath.pathFromNodes?.startsWith(menuItemNodesPath) === true ;
        }
        if (menuItem.type == "Project") {
            active =  this.currentPath.nodesPath == menuItemNodesPath && this.currentNavigationRoute.project == menuItem.route.project;
        }
        if (menuItem.type == "Directory") {
            active =  this.routeService.directoryPathSimilar(this.currentNavigationRoute, menuItem.route) ;
        }
        if (menuItem.type == "Page") {
            active =  this.routeService.pagePathSimilar(this.currentNavigationRoute, menuItem.route) ;
        }

        return active;
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

    calculatePadding(): number {
        return 2 + ((this.menuItem.depth) * this.paddingByDepth);
    }

    trackMenuItem(index: number, item: MenuHierarchy) {
        return item.name;
    }

    branchComparator(branch1: MenuHierarchy, branch2: MenuHierarchy): boolean {
        return branch1.name === branch2.name;
    }

}
