import { Component} from '@angular/core';
import {ConfigService} from './_services/config.service';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  logoSrc: string;
  appTitle: string;

  constructor(private applicationService: ConfigService, private title: Title) {
    this.applicationService.getConfigs().subscribe(result => {
      this.title.setTitle(result.windowTitle);
      this.appTitle = result.title;
      this.logoSrc = result.logoSrc;
      this.favIcon = result.faviconSrc;

      document.documentElement.style.setProperty('--custom-color-main', result.colorMain);
      document.documentElement.style.setProperty('--custom-color-light', result.colorLight);
      document.documentElement.style.setProperty('--custom-color-dark', result.colorDark);
    });
  }

  set favIcon(faviconSrc: string) {
    const linkIcon = document.getElementById('link_icon');
    if (linkIcon) {
      linkIcon.setAttribute('href', faviconSrc);
    }
  }
}
