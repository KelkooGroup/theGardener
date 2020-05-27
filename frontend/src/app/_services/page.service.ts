import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DirectoryApi, PageApi} from '../_models/hierarchy';
import {map} from 'rxjs/operators';
import {Page} from '../_models/page';
import {RouteService} from "./route.service";

@Injectable({
  providedIn: 'root'
})
export class PageService {
  constructor(private http: HttpClient,
              private routeService: RouteService) {
  }

  getRootDirectoryForPath(path: string): Observable<DirectoryApi> {
    const url = `api/directories`;
    const params = new HttpParams().set('path', this.routeService.urlToRelativePath(path));
    return this.http.get<Array<DirectoryApi>>(url, {params})
      .pipe(
        map(res => res[0])
      );
  }

  getPage(path: string): Observable<Page> {
    const url = `api/pages`;
    const params = new HttpParams().set('path', this.routeService.urlToRelativePath(path));
    return this.http.get<Array<PageApi>>(url, {params})
      .pipe(
        map(res => res[0]),
        map(page => {
          const res: Page = {
            title: this.getPageTitle(page),
            path: page.path,
            order: page.order,
            parts: page.content,
            sourceUrl: page.sourceUrl
          };
          return res;
        })
      );
  }

  private getPageTitle(page: PageApi): string {
    if (page.content && page.content.length === 1 && page.content[0].type === 'includeExternalPage') {
      return undefined;
    } else {
      return page.description;
    }
  }
}
