import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SEARCH_PATH } from '../../../_services/route.service';

@Component({
  selector: 'app-search-query',
  templateUrl: './search-query.component.html',
  styleUrls: ['./search-query.component.scss']
})
export class SearchQueryComponent implements OnInit {
  @Input() keyword: string;

  constructor(private activatedRoute: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    if (this.activatedRoute.queryParams) {
      this.activatedRoute.queryParams.subscribe(queryParams => {
        this.keyword = queryParams['keyword'];
      });
    }
  }

  search() {
    if (this.keyword !== '') {
      this.router.navigateByUrl(SEARCH_PATH + `?keyword=${this.keyword.trim()}`);
    }
  }
}
