import { Component, Input } from '@angular/core';
import { SearchResult } from '../../../_models/search';

@Component({
  selector: 'app-search-results',
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.scss']
})
export class SearchResultsComponent {
  @Input() searchResult: SearchResult;
}
