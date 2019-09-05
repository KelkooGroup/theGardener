import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {catchError, map, switchMap} from 'rxjs/operators';
import {combineLatest, of, Subscription} from 'rxjs';
import {PageService} from '../../_services/page.service';
import {PageApi} from '../../_models/hierarchy';

@Component({
  selector: 'app-page-content',
  templateUrl: './page-content.component.html',
  styleUrls: ['./page-content.component.scss']
})
export class PageContentComponent implements OnInit, OnDestroy {
  page: PageApi;
  private subscription: Subscription;

  constructor(private activatedRoute: ActivatedRoute,
              private pageService: PageService) {
  }

  ngOnInit() {
    this.subscription = combineLatest([
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
        console.log(pageRoute);
        if (pageRoute.path && pageRoute.path.endsWith('/')) {
          return this.pageService.getPage(`${pageRoute.path}${pageRoute.page}`);
        } else {
          return of<PageApi>();
        }
      }),
      catchError(err => {
        console.error(`Error while loading page on activatedRoute with params ${JSON.stringify(this.activatedRoute.snapshot.params)} and parent ${JSON.stringify(this.activatedRoute.parent.snapshot.params)}`, err);
        return of<PageApi>();
      })
    ).subscribe(page => {
      this.page = page;
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

}
