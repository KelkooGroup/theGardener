import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UrlCleanerService {

  constructor() {
  }

  relativePathToUrl(relativePath: string): string {
    return relativePath.replace(/_/g, '~').replace(/\//g, '_');
  }

  urlToRelativePath(relativePath: string): string {
    return relativePath.replace(/_/g, '/').replace(/~/g, '_');
  }
}
