import {Component, ElementRef, OnInit, Renderer2, ViewChild} from '@angular/core';
import {ConfigService} from './_services/config.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  headerHeight = 70;
  contentHeight: number;
  footerHeight = 40;
  logoSrc: string;
  apptitle: string;

  @ViewChild('content', {static: true}) content: ElementRef;

  constructor(private renderer: Renderer2, private applicationService: ConfigService) {
    this.applicationService.getConfigs().subscribe(result => {
      this.apptitle = result.title;
      this.logoSrc = result.logoSrc;
    });
  }

  ngOnInit() {
    this.resize();
  }

  onResize(event: any) {
    this.resize();
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
