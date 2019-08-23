import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Config} from '../_models/config';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  constructor(private http: HttpClient) {
  }

  getConfigs(): Observable<Config> {
    const url = 'api/config';
    return this.http.get<Config>(url);
  }

}
