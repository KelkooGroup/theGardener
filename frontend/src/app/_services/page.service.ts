import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DirectoryApi, MarkdownSettings, PageApi} from '../_models/hierarchy';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class PageService {

  constructor(private http: HttpClient) { }

  getRootDirectoryForPath(path: string): Observable<DirectoryApi> {
    const url = `api/directories`;
    const params = new HttpParams().set('path', path);
    return this.http.get<Array<DirectoryApi>>(url, {params})
      .pipe(
        map(res => res[0])
      );
  }

  getPage(path: string): Observable<PageApi> {
    const url = `api/pages`;
    const params = new HttpParams().set('path', path);
    return this.http.get<Array<PageApi>>(url, {params})
      .pipe(
        map(res => res[0]),
        map(page => this.parseTheGardenerMarkdown(page))
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
    console.log(settings);
    return settings;
  }
}

const THE_GARDENER = /'''thegardener((.)*(\n)*)*'''/;
const THE_GARDENER_START = '\'\'\'thegardener';
const THE_GARDENER_END = '\'\'\'';
