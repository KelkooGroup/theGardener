import { Pipe, PipeTransform } from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

@Pipe({
  name: 'removehtmlsanitizer'
})
export class RemoveHtmlSanitizerPipe implements PipeTransform {

  constructor(private sanitizer: DomSanitizer) {}

  transform(html: string): any {
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }

}
