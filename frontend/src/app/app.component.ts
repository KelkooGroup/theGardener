import { Component } from '@angular/core';
import { ConfigService } from './_services/config.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  logoSrc: string;
  appTitle: string;
  baseUrl: string;
  translateTo?: string;
  translateTemplate?: string;

  constructor(private applicationService: ConfigService, private title: Title) {
    const config = this.applicationService.getConfig();

    this.title.setTitle(config.windowTitle);
    this.appTitle = config.title;
    this.logoSrc = config.logoSrc;
    this.favIcon = config.faviconSrc;
    this.baseUrl = config.baseUrl;
    this.translateTo = config.translateTo;
    this.translateTemplate = config.translateTemplate;

    document.documentElement.style.setProperty('--custom-color-main', config.colorMain);
    document.documentElement.style.setProperty('--custom-color-light', config.colorLight);
    document.documentElement.style.setProperty('--custom-color-dark', config.colorDark);
  }

  set favIcon(faviconSrc: string) {
    const linkIcon = document.getElementById('link_icon');
    if (linkIcon) {
      linkIcon.setAttribute('href', faviconSrc);
    }
  }
}
