import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {PageService} from '../../../_services/page.service';
import {map, switchMap} from 'rxjs/operators';
import {PageApi} from '../../../_models/hierarchy';

@Component({
  selector: 'app-navigate-content',
  templateUrl: './navigate-content.component.html',
  styleUrls: ['./navigate-content.component.scss']
})
export class NavigateContentComponent implements OnInit {

  pages: Array<PageApi>;

  constructor(private activatedRoute: ActivatedRoute,
              private pageService: PageService) {
  }

  ngOnInit() {
    this.activatedRoute.params
      .pipe(
        map(params => params['path']),
        switchMap((path: string) => this.pageService.getDirectoriesForPath(path))
      ).subscribe(res => {
      if (res && res.length === 1)
        this.pages = res[0].pages;
      else
        this.pages = [];
    });
  }

  trackByPage(index: number, item: PageApi) {
    return item.name;
  }

  get orderedPages() {
    return this.pages && this.pages.sort((p1, p2) => p1.order > p2.order ? 1 : p1.order === p2.order ? 0 : -1);
  }
}
