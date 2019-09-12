import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DirectoryApi, Page, PageApi, PagePart} from '../_models/hierarchy';
import {map} from 'rxjs/operators';
import {UrlCleanerService} from './url-cleaner.service';
import {MarkdownParserService} from './markdown-parser.service';

@Injectable({
  providedIn: 'root'
})
export class PageService {
  constructor(private http: HttpClient,
              private markdownParser: MarkdownParserService,
              private urlCleaner: UrlCleanerService) {
  }

  getRootDirectoryForPath(path: string): Observable<DirectoryApi> {
    const url = `api/directories`;
    const params = new HttpParams().set('path', this.urlCleaner.urlToRelativePath(path));
    return this.http.get<Array<DirectoryApi>>(url, {params})
      .pipe(
        map(res => res[0])
      );
  }

  getPage(path: string): Observable<Page> {
    const url = `api/pages`;
    const params = new HttpParams().set('path', this.urlCleaner.urlToRelativePath(path));
    return this.http.get<Array<PageApi>>(url, {params})
      .pipe(
        map(res => res[0]),
        map(page => {
          const pageParts = this.markdownParser.parseMarkdown(page.markdown);
          const res: Page = {
            title: this.getPageTitle(page, pageParts),
            path: page.path,
            order: page.order,
            parts: pageParts
          };
          return res;
        })
      );
  }

  private getPageTitle(page: PageApi, pageParts: Array<PagePart>): string {
    if (pageParts.length === 1 && pageParts[0].type === 'ExternalLink' ) {
      return undefined;
    } else {
      return page.description;
    }
  }
}
