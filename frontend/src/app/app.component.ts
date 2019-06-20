import {Component, ElementRef, OnInit, Renderer2, ViewChild} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  headerHeight = 70;
  contentHeight: number;
  footerHeight = 40;

  @ViewChild('content', { static: true }) content: ElementRef;

  constructor(private renderer: Renderer2) {
  }

  ngOnInit() {
    this.resize();
  }

  onResize(event: any) {
    this.resize();
  }

  resize() {
    this.contentHeight = window.innerHeight - this.headerHeight - this.footerHeight ;
    console.log('Resize content: ' + this.contentHeight);
    this.renderer.setStyle(
      this.content.nativeElement,
      'height',
      this.contentHeight + 'px'
    );

  }


}
