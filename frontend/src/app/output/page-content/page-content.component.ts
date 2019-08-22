import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {map, switchMap} from 'rxjs/operators';
import {combineLatest} from 'rxjs';
import {PageService} from '../../_services/page.service';
import {PageApi} from '../../_models/hierarchy';

@Component({
  selector: 'app-page-content',
  templateUrl: './page-content.component.html',
  styleUrls: ['./page-content.component.scss']
})
export class PageContentComponent implements OnInit {
  page: PageApi;

  constructor(private activatedRoute: ActivatedRoute,
              private pageService: PageService) {
  }

  ngOnInit() {
    combineLatest(
      this.activatedRoute.parent.params,
      this.activatedRoute.params
    ).pipe(
      map(([parentParams, params]) => {
        const name = parentParams['name'];
        const path = parentParams['path'];
        const page = params['page'];
        return {name, path, page};
      }),
      switchMap(pageRoute => this.pageService.getPage(`${pageRoute.path}${pageRoute.page}`))
    ).subscribe(page => {
        if (page && page.length === 1) {
          this.page = page[0];
        }
      });
  }

}
