import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DirectoryApi, Page, PageApi} from '../_models/hierarchy';
import {map} from 'rxjs/operators';
import {UrlCleanerService} from './url-cleaner.service';
import {MarkdownParserService} from './markdown-parser.service';

@Injectable({
  providedIn: 'root'
})
export class PageService {
  constructor(private http: HttpClient,
              private markdownParser: MarkdownParserService
              private urlCleaner: UrlCleanerService) { }

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
            title: page.description,
            path: page.path,
            order: page.order,
            parts: pageParts
          };
          return res;
        })
      );
  }

  private parseTheGardenerMarkdown(input: PageApi): PageApi {
    if (input.markdown.match(THE_GARDENER)) {
      const settings = this.parseTheGardenerSettings(input.markdown);
      if (settings.include) {
        const pageNode = Object.assign({}, input);
        pageNode.markdown = '';
        pageNode.externalLink = settings.include.url;
        return pageNode;
      }
    }
    return input;
  }

  private parseTheGardenerSettings(theGardener: string): MarkdownSettings {
    const settingsString = theGardener
      .replace(THE_GARDENER_START, '')
      .replace(THE_GARDENER_END, '');
    const settings = JSON.parse(settingsString) as MarkdownSettings;
    return settings;
  }
}

const THE_GARDENER = /```thegardener((.)*(\n)*)*```/;
const THE_GARDENER_START = '```thegardener';
const THE_GARDENER_END = '```';
