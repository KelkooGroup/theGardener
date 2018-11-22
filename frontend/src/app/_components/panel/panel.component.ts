import {Component, Input, OnInit} from '@angular/core';


@Component({
  selector: 'app-panel',
  templateUrl: './panel.component.html',
  styleUrls: ['./panel.component.scss']
})
export class PanelComponent implements OnInit {

  @Input() showSpinner?: boolean = false;
  @Input() panelTitle: string;
  @Input() panelIcon: string;

  @Input() isClosable?: boolean = true;

  @Input() isClosed?: boolean = false;

  constructor() {
  }

  ngOnInit() {
  }

  toggleClosed() {
    this.isClosed = !this.isClosed;
  }

}
