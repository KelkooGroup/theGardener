import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DirectoryApi, PageApi} from '../_models/hierarchy';
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
        map(res => res[0])
      );
  }
}
