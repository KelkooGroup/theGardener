import {Component, Input, OnInit} from '@angular/core';


@Component({
  selector: 'app-panel',
  templateUrl: './panel.component.html',
  styleUrls: ['./panel.component.scss']
})
export class PanelComponent implements OnInit {

  @Input() scrollYIfNeeded ? = false;
  @Input() showSpinner ? = false;
  @Input() showProgressBar ? = false;

  @Input() panelTitle: string;
  @Input() panelIcon: string;

  @Input() isClosable ? = true;

  @Input() isClosed ? = false;

  constructor() {
  }

  ngOnInit() {
  }

  toggleClosed() {
    this.isClosed = !this.isClosed;
  }

}
