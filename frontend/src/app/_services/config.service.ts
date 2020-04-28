import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Config} from '../_models/config';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  config?: Config;

  constructor(private http: HttpClient) {
    this.getConfigs().subscribe(result => {
      this.config = result;
    });
  }

  getConfigs(): Observable<Config> {
    const url = 'api/config';
    return this.http.get<Config>(url);
  }

}
