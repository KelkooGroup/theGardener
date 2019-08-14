import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {MenuService} from '../../../_services/menu.service';
import {map, switchMap} from 'rxjs/operators';
import {MenuHierarchy} from '../../../_models/menu';

@Component({
  selector: 'app-navigate-menu',
  templateUrl: './navigate-menu.component.html',
  styleUrls: ['./navigate-menu.component.scss'],
})
export class NavigateMenuComponent implements OnInit {
  res: MenuHierarchy[];
  depth: number;
  expanded: boolean;

  constructor(private activatedRoute: ActivatedRoute,
              private menuService: MenuService,
              public router: Router) {
  }

  ngOnInit() {
    this.activatedRoute.params
      .pipe(
        map(params => params['name']),
        switchMap((nodeName: string) => this.menuService.getMenuHierarchyForSelectedNode(nodeName))
      )
      .subscribe(res => {
        console.log(res);
        this.res = res;
      });
  }
}
