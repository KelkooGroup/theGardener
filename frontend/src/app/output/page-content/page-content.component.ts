import { Component, NgZone, OnDestroy, OnInit, AfterViewChecked } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { catchError, map, switchMap } from 'rxjs/operators';
import { combineLatest, EMPTY, empty, of, Subscription } from 'rxjs';
import { PageService } from '../../_services/page.service';
import { IncludeExternalPagePart, MarkdownPart, OpenApiPart, OpenApiPathPart, Page, PagePart, ScenarioPart } from '../../_models/page';
import { RouteService, SEARCH_PATH } from '../../_services/route.service';
import { NavigationRoute } from '../../_models/route';

@Component({
  selector: 'app-page-content',
  templateUrl: './page-content.component.html',
  styleUrls: ['./page-content.component.scss']
})
export class PageContentComponent implements OnInit, OnDestroy, AfterViewChecked {
  page: Page;
  private subscription: Subscription;
  private fragmentSubscription: Subscription;
  private routerSubscription: Subscription;
  private fragment: string;
  private canScroll = true;
  private targetedRoute: NavigationRoute;

  constructor(
    private activatedRoute: ActivatedRoute,
    private pageService: PageService,
    private routeService: RouteService,
    private router: Router,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    window['navigateTo'] = navigateTo(this.router, this.ngZone);
    window.addEventListener('scroll', this.scroll, { capture: true });
    this.fragmentSubscription = this.activatedRoute.fragment.subscribe(fragment => {
      this.fragment = fragment;
      this.canScroll = true;
    });
    this.routerSubscription = this.router.events.subscribe(evt => {
      if (evt instanceof NavigationEnd) {
        if (!this.fragment) {
          const cmp = document.getElementById('top-page');
          if (cmp) {
            cmp.scrollIntoView();
          }
        }
      }
    });
    this.subscription = combineLatest([this.activatedRoute.params])
      .pipe(
        map(([params]) => params),
        switchMap(params => {
          this.targetedRoute = this.routeService.navigationParamsToNavigationRoute(params);
          if (this.targetedRoute.page === undefined) {
            return EMPTY;
          } else {
            const backEndPath = this.routeService.navigationRouteToBackEndPath(this.targetedRoute);
            return this.pageService.getPage(backEndPath.pathFromProject);
          }
        }),
        catchError(() => {
          const keyword = this.routeService.extractKeyword(this.targetedRoute);
          this.router.navigateByUrl(SEARCH_PATH + `?keyword=${keyword.trim()}`);
          return EMPTY;
        })
      )
      .subscribe(page => {
        this.page = page;
      });
  }

  ngAfterViewChecked(): void {
    if (this.canScroll) {
      if (this.fragment) {
        const cmp = document.getElementById(this.fragment);
        if (cmp) {
          cmp.scrollIntoView();
        }
      }
    }
  }

  ngOnDestroy(): void {
    if (this.fragmentSubscription) {
      this.fragmentSubscription.unsubscribe();
    }
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.routerSubscription) {
      this.routerSubscription.unsubscribe();
    }
    window.removeEventListener('scroll', this.scroll, true);
  }

  scroll = (): void => {
    this.canScroll = false;
  };

  getExternalLink(part: PagePart) {
    return (part.data as IncludeExternalPagePart).includeExternalPage;
  }

  getMarkdown(part: PagePart) {
    return (part.data as MarkdownPart).markdown;
  }

  getScenario(part: PagePart) {
    return (part.data as ScenarioPart).scenarios;
  }

  getOpenApiModel(part: PagePart) {
    return (part.data as OpenApiPart).openApi;
  }

  getOpenApiPaths(part: PagePart) {
    return (part.data as OpenApiPathPart).openApiPath;
  }

  getPosition(part: PagePart) {
    return this.page.parts.indexOf(part);
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
      const targetUrl = RouteService.legacyFullFrontEndUrlToFullFrontEndUrl(path);
      router.navigateByUrl(targetUrl);
    }
  }
}
