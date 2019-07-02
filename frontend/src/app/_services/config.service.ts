import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Config} from '../_models/config';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private url: string;

  constructor(private http: HttpClient) {
    this.url = 'api/config';
  }

  getConfigs(): Observable<Config> {
    return this.http.get<Config>(this.url);
  }


}
