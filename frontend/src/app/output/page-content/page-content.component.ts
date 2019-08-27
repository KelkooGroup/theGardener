import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {catchError, map, switchMap} from 'rxjs/operators';
import {combineLatest, of} from 'rxjs';
import {PageService} from '../../_services/page.service';
import {PageApi} from '../../_models/hierarchy';
import {NotificationService} from '../../_services/notification.service';

@Component({
  selector: 'app-page-content',
  templateUrl: './page-content.component.html',
  styleUrls: ['./page-content.component.scss']
})
export class PageContentComponent implements OnInit {
  page: PageApi;

  constructor(private activatedRoute: ActivatedRoute,
              private pageService: PageService,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    combineLatest([
      this.activatedRoute.parent.params,
      this.activatedRoute.params
    ]).pipe(
      map(([parentParams, params]) => {
        const name = parentParams.name;
        const path = parentParams.path;
        const page = params.page;
        return {name, path, page};
      }),
      switchMap(pageRoute => {
        if (pageRoute.path.endsWith('/')) {
          return this.pageService.getPage(`${pageRoute.path}${pageRoute.page}`);
        } else {
          return of<PageApi>();
        }
      }),
      catchError(err => {
        this.notificationService.showError(`Error while loading page`, err);
        return of<PageApi>();
      })
    ).subscribe(page => {
      this.page = page;
    });
  }

}
