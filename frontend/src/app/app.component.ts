import {Component, ElementRef, OnInit, Renderer2, ViewChild} from '@angular/core';
import {ConfigService} from './_services/config.service';
import {Title} from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  headerHeight = 112;
  contentHeight: number;
  footerHeight = 40;
  logoSrc: string;
  apptitle: string;

  @ViewChild('content', {static: true}) content: ElementRef;

  constructor(private renderer: Renderer2, private applicationService: ConfigService, private title: Title) {
    this.applicationService.getConfigs().subscribe(result => {
      this.title.setTitle(result.windowTitle);
      this.apptitle = result.title;
      this.logoSrc = result.logoSrc;
      this.favIcon = result.faviconSrc;


    });
  }

  ngOnInit() {
    this.resize();
  }

  onResize(event: any) {
    this.resize();
  }

  set favIcon(faviconSrc: string) {
    const linkIcon = document.getElementById('link_icon');
    if (linkIcon) {
      linkIcon.setAttribute('href', faviconSrc);
    }
  }

  resize() {
    this.contentHeight = window.innerHeight - this.headerHeight - this.footerHeight;
    this.renderer.setStyle(
      this.content.nativeElement,
      'height',
      this.contentHeight + 'px'
    );

  }


}
