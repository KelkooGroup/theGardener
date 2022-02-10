import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Config } from '../_models/config';
import { NotificationService } from './notification.service';
import { lastValueFrom } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  config: Config;

  constructor(private http: HttpClient, private notificationService: NotificationService) {}

  // This is the method you want to call at bootstrap
  // Important: It should return a Promise
  load(): Promise<Config> {
    this.config = null;

    return lastValueFrom(this.http
      .get<Config>('api/config'))
      .then((data: any) => {
        this.config = data;
        return data;
      })
      .catch((err: any) => {
        this.notificationService.showError('Error initializing the app', err);
        return Promise.resolve();
      });
  }

  getConfig(): Config {
    return this.config;
  }
}
