import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {environment} from '../environments/environment';

@Injectable()
export class APIInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let url: string;
    if (req.url.startsWith('http')) {
      url = req.url;
    } else {
      url = `${environment.apiUrl}/${req.url}`;
    }
    const apiReq = req.clone({url: url, withCredentials: true});
    return next.handle(apiReq);
  }
}
