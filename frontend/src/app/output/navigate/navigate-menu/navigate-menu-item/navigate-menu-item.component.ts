import {Component, Input, OnInit} from '@angular/core';
import {MenuHierarchy, MenuProjectHierarchy} from '../../../../_models/menu';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-navigate-menu-item',
  templateUrl: './navigate-menu-item.component.html',
  styleUrls: ['./navigate-menu-item.component.scss'],
  animations: [
    trigger('indicatorRotate', [
      state('collapsed', style({transform: 'rotate(0deg)'})),
      state('expanded', style({transform: 'rotate(180deg)'})),
      transition('expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4,0.0,0.2,1)')
      ),
    ])
  ]
})
export class NavigateMenuItemComponent implements OnInit {

  @Input() menuItem: MenuHierarchy;
  expanded: boolean;
  active: boolean;
  nodeNameInUrl: string;
  pathInUrl: string;
  selectedBranch: MenuHierarchy;

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit() {
    this.activatedRoute.params
      .subscribe(params => {
        this.nodeNameInUrl = params['name'];
        this.pathInUrl = params['path'];
        this.active = this.isNodeActive(this.menuItem);
        this.expanded = this.menuItem.type === 'Node' || this.active;
        if (this.menuItem.type === 'Project') {
          const project = this.menuItem as MenuProjectHierarchy;
          this.selectedBranch = project.children.find(b => b.name === project.stableBranch)
        }
      });
  }

  navigateToItem() {
    if (this.menuItem.route !== undefined) {
      const targetUrl = `app/documentation/navigate/${this.nodeNameInUrl}`;
      this.router.navigate([targetUrl, {path: this.menuItem.route}]);
    } else {
      this.expanded = !this.expanded;
    }
  }

  isRouteInActivatedUrl(node: MenuHierarchy) {
    const isRouteInActivatedUrl = node.route !== undefined &&
      this.pathInUrl !== undefined &&
      (this.pathInUrl === node.route ||
        // Angular router automatically removes trailing slash from URL so we need to append it to check path
        this.pathInUrl.concat('/') === node.route );
    return isRouteInActivatedUrl;
  }

  isNodeActive(node: MenuHierarchy): boolean {
    const hasActivatedChild = node.children.some(c => this.isNodeActive(c));
    return this.isRouteInActivatedUrl(node) || hasActivatedChild;
  }
}
