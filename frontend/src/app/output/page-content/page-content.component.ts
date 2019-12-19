import {Component, NgZone, OnDestroy, OnInit, AfterViewChecked} from '@angular/core';
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
export class PageContentComponent implements OnInit, OnDestroy, AfterViewChecked {
  page: Page;
  private subscription: Subscription;
  private fragmentSubscription: Subscription;
  private fragment: string;

  constructor(private activatedRoute: ActivatedRoute,
              private pageService: PageService,
              private notificationService: NotificationService,
              private router: Router,
              private ngZone: NgZone) {
  }

  ngOnInit() {
    // @ts-ignore
    window.navigateTo = navigateTo(this.router, this.ngZone);
    this.fragmentSubscription = this.activatedRoute.fragment.subscribe(fragment => {
      this.fragment = fragment;
    });
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
          return this.pageService.getPage(`${pageRoute.path}${pageRoute.page}`)
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

  ngAfterViewChecked(): void {
    if (this.fragment) {
      const cmp = document.getElementById(this.fragment);
      if (cmp) {
        cmp.scrollIntoView();
      }
    }
  }

  ngOnDestroy(): void {
    this.fragmentSubscription.unsubscribe();
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
  ngZone.run(() => {
    PageContentComponentTools.navigate(router, path);
  });
};

export class PageContentComponentTools {

  static navigate(router: Router, path: string) {
    if (path) {
      const pathParams = path.split(';');
      if (pathParams.length > 1) {
        const slashes = pathParams[1].split('/');
        if (slashes.length > 1) {
          const fragment = slashes[slashes.length - 1].split('#');
          router.navigate([pathParams[0], {path: decodeURIComponent(slashes[0].replace('path=', ''))}, fragment[0]], {fragment: fragment[1]}).catch(err => console.log(err));
        }
      }
    }
  }

}
