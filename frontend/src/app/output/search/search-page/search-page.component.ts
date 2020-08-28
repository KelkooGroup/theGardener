import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {PageService} from "../../../_services/page.service";
import {SearchResult} from "../../../_models/search";
import {NotificationService} from "../../../_services/notification.service";

@Component({
  selector: 'app-search-page',
  templateUrl: './search-page.component.html',
  styleUrls: ['./search-page.component.scss']
})
export class SearchPageComponent implements OnInit {

  @Input() keyword: string;
  searchResult: SearchResult;

  constructor(private activatedRoute: ActivatedRoute, private notificationService: NotificationService, private pageService: PageService) { }

  ngOnInit(): void {
    this.getKeyword();
    this.pageService.searchPages(this.keyword) .subscribe(
        result => {
          this.searchResult = result;
        }, error => {
          this.notificationService.showError('Error while searching in the page index', error);
        });
  }

  getKeyword() {
    if (this.activatedRoute.firstChild && this.activatedRoute.firstChild.snapshot) {
      this.keyword = this.activatedRoute.firstChild.snapshot.queryParamMap.get("keyword");
    }
    if (!this.keyword && this.activatedRoute && this.activatedRoute.snapshot) {
      this.keyword = this.activatedRoute.snapshot.queryParamMap.get("keyword");
    }
  }




}
