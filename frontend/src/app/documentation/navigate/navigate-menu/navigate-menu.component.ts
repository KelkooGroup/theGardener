import {
  AfterViewChecked,
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren
} from '@angular/core';
import {Router} from '@angular/router';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {NavigationItem} from "../../../_services/criterias-selection";
import {MatSelect} from "@angular/material";

@Component({
  selector: 'app-navigate-menu',
  templateUrl: './navigate-menu.component.html',
  styleUrls: ['./navigate-menu.component.scss'],
  animations: [
    trigger('indicatorRotate', [
      state('collapsed', style({transform: 'rotate(0deg)'})),
      state('expanded', style({transform: 'rotate(180deg)'})),
      transition('expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4,0.0,0.2,1)')
      ),
    ])
  ]
})
export class NavigateMenuComponent implements OnInit, AfterViewChecked {

  expanded: boolean;
  @HostBinding('attr.aria-expanded') ariaExpanded = this.expanded;
  @Input() item: NavigationItem;
  @Input() depth: number;

  @Input() selectedOption: NavigationItem;

  itemOptionPlaceHolder: string;

  @ViewChildren(NavigateMenuComponent)
  items: QueryList<NavigateMenuComponent>;

  @ViewChild(MatSelect)
  selectOptions : MatSelect;

  needSomeChecksAfterRendering = false;
  pageToBeChecked : string;

  constructor(public router: Router) {
    if (this.depth === undefined) {
      this.depth = 0;
    }
  }

  ngOnInit() {
    if (this.item.itemOptions) {
      this.selectedOption = this.item.itemOptionSelected();
      this.itemOptionPlaceHolder = this.item.itemOptionPlaceHolder;
    }
  }

  @Output()
  selection: EventEmitter<NavigationItem> = new EventEmitter();

  navigateTo(page: string) {
    this.expanded = this.depth < 2;
    if (page && page.startsWith(this.item.route)) {
      if (this.item.itemChildren().length > 0) {
        this.expanded = true;
      }
      this.items.forEach(e => e.navigateTo(page));
      if (page === this.item.route) {
        this.selection.emit(this.item);
        this.item.selected = true;
      }

      if (page && this.item && this.item.itemOptions) {
        var that: NavigateMenuComponent = this;
        this.item.itemChildren().forEach(function (item) {
          if (page.startsWith(item.route)) {
            if (item.itemChildren().length > 0) {
              that.expanded = true;
            }
            that.selectedOption = item;
            if (that.selectOptions){
              that.selectOptions.value = item;
            }
            that.items.forEach(e => e.navigateTo(page));
            that.pageToBeChecked = page;
            that.needSomeChecksAfterRendering = true;
          }
        });
      }
    }else{
      this.items.forEach(e => e.navigateTo(page));
    }
  }

  onItemSelected(item: NavigationItem) {
    if (item.itemChildren().length) {
      this.expanded = !this.expanded;
    }
    this.selection.emit(item);
    this.item.selected = true;
  }

  selectionFromChild(selection: NavigationItem) {
    this.selection.emit(selection);
  }

  setSelectedOption(item: NavigationItem) {
    this.selectedOption = item;
  }

  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  ngAfterViewChecked() {
    if (this.needSomeChecksAfterRendering) {
      this.needSomeChecksAfterRendering = false;
      (async () => {
        await this.delay(1);
        console.log(`Opening options to ${this.pageToBeChecked}`);
        this.items.forEach(e => e.navigateTo(this.pageToBeChecked));
      })();
    }
  }

}
