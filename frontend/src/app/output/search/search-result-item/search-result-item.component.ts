import {Component, Input, OnInit} from '@angular/core';
import {PageIndexDocument, SearchResultItem} from "../../../_models/search";
import {NAVIGATE_PATH, RouteService} from "../../../_services/route.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-search-result-item',
  templateUrl: './search-result-item.component.html',
  styleUrls: ['./search-result-item.component.scss']
})
export class SearchResultItemComponent implements OnInit {

  @Input()  item: SearchResultItem;
  @Input()  index: number;


  constructor(private routeService: RouteService,private router: Router) { }

  ngOnInit(): void {
  }

  navigateTo(page: PageIndexDocument){
    let path = this.routeService.backEndHierarchyAndPathToFrontEndPath(page.hierarchy,page.path);
    this.router.navigateByUrl(  NAVIGATE_PATH + path);
  }

  showIndex(): string {
    return (this.index + 1).toString()
  }

}
