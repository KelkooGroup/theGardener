import { TestBed } from '@angular/core/testing';

import { UrlCleanerService } from './url-cleaner.service';

describe('UrlCleanerService', () => {
  let service: UrlCleanerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.get(UrlCleanerService);
  });

  it('should transform relative path to clean URL', () => {
    expect(service.relativePathToUrl('/features/foo/bar')).toEqual('_features_foo_bar');
    expect(service.relativePathToUrl('/features/foo_foo/bar')).toEqual('_features_foo~foo_bar');
  });

  it('should transform URL to relative path', () => {
    expect(service.urlToRelativePath('_features_foo_bar')).toEqual('/features/foo/bar');
    expect(service.urlToRelativePath('_features_foo~foo_bar')).toEqual('/features/foo_foo/bar');
  });
});
