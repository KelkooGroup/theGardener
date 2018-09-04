import { Component, OnInit, Input } from '@angular/core';

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

  constructor() { }

  ngOnInit() {
  }

}

