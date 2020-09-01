

export interface SearchResult {
  items: Array<SearchResultItem>;
}

export interface SearchResultItem {
  page: PageIndexDocument;
  highlights: Array<HighlightedFragment>;
}

export interface PageIndexDocument {
  id: string;
  hierarchy: string;
  path: string;
  breadcrumb: string;
  project: string;
  branch: string;
  label: string;
  description: string;
  pageContent: string;
}

export interface HighlightedFragment {
  fragment: string;
  word: string;
}
