import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PageService } from '../../../_services/page.service';
import { SearchResult } from '../../../_models/search';
import { NotificationService } from '../../../_services/notification.service';

@Component({
  selector: 'app-search-page',
  templateUrl: './search-page.component.html',
  styleUrls: ['./search-page.component.scss']
})
export class SearchPageComponent implements OnInit {
  @Input() keyword: string;
  searchResult: SearchResult;

  constructor(private activatedRoute: ActivatedRoute, private notificationService: NotificationService, private pageService: PageService) {}

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(queryParams => {
      this.keyword = queryParams['keyword'];
      this.search();
    });
  }

  private search() {
    this.pageService.searchPages(this.keyword).subscribe({
      next: (result) => {
        this.searchResult = result;
      },
      error: (error) => {
        this.notificationService.showError('Error while searching in the page index', error);
      }
    });
  }
}
