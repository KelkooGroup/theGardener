import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DirectoryApi, PageApi} from '../_models/hierarchy';

@Injectable({
  providedIn: 'root'
})
export class PageService {

  constructor(private http: HttpClient) { }

  getDirectoriesForPath(path: string): Observable<Array<DirectoryApi>> {
    const url = `api/directories`;
    const params = new HttpParams().set('path', path);
    return this.http.get<Array<DirectoryApi>>(url, {params});
  }

  getPage(path: string): Observable<Array<PageApi>> {
    const url = `api/pages`;
    const params = new HttpParams().set('path', path);
    return this.http.get<Array<PageApi>>(url, {params});
  }
}
