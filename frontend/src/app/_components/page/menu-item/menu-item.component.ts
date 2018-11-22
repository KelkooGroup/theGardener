import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss']
})
export class MenuItemComponent implements OnInit {

  @Input()
  routerlink: string;

  @Input()
  icon: string;

  @Input()
  label: string;

  @Input()
  implemented: boolean = true;

  constructor() {
  }

  ngOnInit() {
  }

}

