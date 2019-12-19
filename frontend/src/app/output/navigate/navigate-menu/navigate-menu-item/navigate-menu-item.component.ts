import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {MenuHierarchy, MenuProjectHierarchy} from '../../../../_models/menu';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {NotificationService} from '../../../../_services/notification.service';

@Component({
  selector: 'app-navigate-menu-item',
  templateUrl: './navigate-menu-item.component.html',
  styleUrls: ['./navigate-menu-item.component.scss'],
})
export class NavigateMenuItemComponent implements OnInit, OnDestroy {

  @Input() menuItem: MenuHierarchy;
  expanded: boolean;
  active: boolean;
  leafSelection: boolean;
  nodeNameInUrl: string;
  pathInUrl: string;
  selectedBranch: MenuHierarchy;
  private subscription: Subscription;

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.subscription = this.activatedRoute.params
      .subscribe(params => {
        this.nodeNameInUrl = params.name;
        this.pathInUrl = params.path;
        this.active = this.isNodeActive(this.menuItem);
        this.leafSelection = this.isLeafSelection(this.menuItem);
        this.expanded = this.menuItem.type === 'Node' || this.active;
        if (this.menuItem.type === 'Project') {
          const project = this.menuItem as MenuProjectHierarchy;
          const branchInUrl = this.getBranchFromUrl();
          const branchToSelect = branchInUrl ? branchInUrl : project.stableBranch;
          this.selectedBranch = project.children.find(b => b.name === branchToSelect);
        }
      }, error => {
        this.notificationService.showError(`Error while showing menu item ${this.menuItem.name}`, error);
      });
  }

  navigateToItem() {
    if (this.menuItem.route !== undefined) {
      if (this.activatedRoute.snapshot && this.activatedRoute.snapshot.params && this.menuItem.route === this.activatedRoute.snapshot.params.path) {
        this.expanded = !this.expanded;
      } else {
        this.router.navigate([this.targetUrl, {path: this.menuItem.route}]);
      }
    } else {
      this.expanded = !this.expanded;
    }
  }

  navigateToSelectedBranch() {
    this.router.navigate([this.targetUrl, {path: this.selectedBranch.route}]);
  }

  private get targetUrl(): string {
    return `app/documentation/navigate/${this.nodeNameInUrl}`;
  }

  isRouteInActivatedUrl(node: MenuHierarchy) {
    const isRouteInActivatedUrl = node.route &&
      this.pathInUrl &&
      (this.pathInUrl === this.getRouteWithBranch(node.route) ||
        // Angular router automatically removes trailing slash from URL so we need to append it to check path
        this.pathInUrl.concat('/') === this.getRouteWithBranch(node.route));
    return isRouteInActivatedUrl;
  }

  isNodeActive(node: MenuHierarchy): boolean {
    const hasActivatedChild = node.children.some(c => this.isNodeActive(c));
    return this.isRouteInActivatedUrl(node) || hasActivatedChild;
  }

  isLeafSelection(node: MenuHierarchy): boolean {
    const hasActivatedChild = node.children.some(c => this.isNodeActive(c));
    return this.isRouteInActivatedUrl(node) && !hasActivatedChild;
  }

  trackMenuItem(index: number, item: MenuHierarchy) {
    return item.name;
  }

  branchComparator(branch1: MenuHierarchy, branch2: MenuHierarchy): boolean {
    return branch1.name === branch2.name;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  calculatePadding(): number {
    return (this.menuItem.depth + 1) * 16 + (+(this.menuItem.children.length === 0) * 12);
  }

  getRouteWithBranch(nodeRoute: string){
    return nodeRoute.replace('>>',`>${this.getBranchFromUrl()}>`)
  }

  private getBranchFromUrl(): string | undefined {
    if (this.pathInUrl) {
      const pathParts = this.pathInUrl.split('>');
      if (pathParts.length > 1) {
        return pathParts[1].replace(/_/g, '/').replace(/~/g, '_');
      } else {
        return undefined;
      }
    } else {
      return undefined;
    }

  }
}
