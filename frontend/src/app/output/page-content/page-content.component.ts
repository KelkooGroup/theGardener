import {Component, NgZone, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {catchError, map, switchMap} from 'rxjs/operators';
import {combineLatest, of, Subscription} from 'rxjs';
import {PageService} from '../../_services/page.service';
import {IncludeExternalPagePart, MarkdownPart, Page, PagePart, ScenarioPart} from '../../_models/page';
import {NotificationService} from '../../_services/notification.service';


@Component({
  selector: 'app-page-content',
  templateUrl: './page-content.component.html',
  styleUrls: ['./page-content.component.scss']
})
export class PageContentComponent implements OnInit, OnDestroy {
  page: Page;
  private subscription: Subscription;

  constructor(private activatedRoute: ActivatedRoute,
              private pageService: PageService,
              private notificationService: NotificationService,
              private router: Router,
              private ngZone: NgZone) {
  }

  ngOnInit() {
    // @ts-ignore
    window.navigateTo = navigateTo(this.router, this.ngZone);
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
        if (pageRoute.path && pageRoute.path.endsWith('_')) {
          return this.pageService.getPage(`${pageRoute.path}${pageRoute.page}`);
        } else {
          return of<Page>();
        }
      }),
      catchError(err => {
        this.notificationService.showError(`Error while loading page`, err);
        return of<Page>();
      })
    ).subscribe(page => {
      this.page = page;
    });
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  getExternalLink(part: PagePart) {
    return (part.data as IncludeExternalPagePart).includeExternalPage;
  }

  getMarkdown(part: PagePart) {
    return (part.data as MarkdownPart).markdown;
  }

  getScenario(part: PagePart) {
    return (part.data as ScenarioPart).scenarios;
  }

}

const navigateTo = (router: Router, ngZone: NgZone) => (path: string) => {
  ngZone.run(() => router.navigateByUrl(path)).catch(err => console.log(err));
};
