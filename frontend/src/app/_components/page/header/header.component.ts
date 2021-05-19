import { Component, Input, OnInit } from '@angular/core';
import { MenuService } from '../../../_services/menu.service';
import { NotificationService } from '../../../_services/notification.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MenuHierarchy } from '../../../_models/menu';
import { NAVIGATE_PATH, RouteService } from '../../../_services/route.service';
import { NavigationParams, NavigationRoute } from '../../../_models/route';
import { MobileMenuHelperService } from '../../../_services/mobile-menu-helper.service';
import ISO6391 from 'iso-639-1';

interface Language {
  code: string;
  name: string;
  url: string;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  @Input() logoSrc: string;

  @Input() appTitle?: string;

  @Input() baseUrl: string;

  @Input() translateTo?: string;

  @Input() translateTemplate?: string;

  items: Array<MenuHierarchy>;
  url: string;

  translateLanguages: Array<Language> = [];
  selectedLanguageCode: string;

  constructor(
    private menuService: MenuService,
    private routeService: RouteService,
    private notificationService: NotificationService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    public mobileMenuHelper: MobileMenuHelperService
  ) {}

  ngOnInit() {
    this.menuService.getMenuHeader().subscribe(
      result => {
        this.items = result;
        if (this.items.length > 0) {
          const currentRoute = this.getCurrentRoute();
          if (!this.url || this.routeService.isNavigationUrl(this.url)) {
            if (!currentRoute.nodes || currentRoute.nodes?.length === 0 || currentRoute.page === undefined) {
              this.navigateTo(this.items[0]);
            }
          }
        }

        const currentUrl = this.baseUrl + '/' + this.url;
        const languageNone = { code: 'none', name: 'None', url: currentUrl };
        this.selectedLanguageCode = languageNone.code;

        if (this.translateTo && this.translateTemplate) {
          this.translateLanguages = this.translateTo.split(',').map(code => {
            const language = {
              code,
              name: ISO6391.getName(code),
              url: this.translateTemplate.replace('{{to}}', code).replace('{{encoded_url}}', encodeURIComponent(currentUrl))
            };

            if (document.location.href.includes(code)) {
              this.selectedLanguageCode = code;
            }

            return language;
          });
        }

        this.translateLanguages.unshift(languageNone);
      },
      error => {
        this.notificationService.showError('Error while getting first level of hierarchy', error);
      }
    );
  }

  getCurrentRoute(): NavigationRoute {
    let params: NavigationParams;
    if (this.activatedRoute.firstChild && this.activatedRoute.firstChild.snapshot) {
      params = this.activatedRoute.firstChild.snapshot.params;
      if (this.activatedRoute.firstChild.snapshot.url) {
        this.url = this.activatedRoute.firstChild.snapshot.url.join('/');
      }
    }
    if (!params && this.activatedRoute && this.activatedRoute.snapshot) {
      params = this.activatedRoute.snapshot.params;
      if (this.activatedRoute.snapshot.url) {
        this.url = this.activatedRoute.snapshot.url.join('/');
      }
    }
    return this.routeService.navigationParamsToNavigationRoute(params);
  }

  navigateTo(item: MenuHierarchy) {
    const itemPath = this.routeService.menuHierarchyToFrontEndPath(item);
    this.router.navigateByUrl(NAVIGATE_PATH + itemPath.pathFromNodes);
  }

  isItemInActivatedRoute(item: MenuHierarchy) {
    return this.getCurrentRoute().nodes[0] === item.name;
  }

  switchLanguage(code: string) {
    const selectedLanguage = this.translateLanguages.find(language => language.code === code);
    if (selectedLanguage) {
      document.location.href = selectedLanguage.url;
    }
  }
}
