import { Pipe, PipeTransform } from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Pipe({
  name: 'anchor'
})
export class AnchorPipe implements PipeTransform {

  constructor(private activatedRoute: ActivatedRoute) {
  }


  transform(value: string): string {
    const linkRegexString = '<h[0-9] id="(\\S*)?">.*?<\\/h[0-9]>';
    const linkRegex = new RegExp(linkRegexString, 'g');
    const path = this.activatedRoute.parent.snapshot.params.path;
    const hierarchy = this.activatedRoute.parent.snapshot.params.name;
    const page = this.activatedRoute.snapshot.params.page;
    const currentUrl = `app/documentation/navigate/${hierarchy};path=${path}/${page}`;
    function replacer(fullMatch: string, titleId: string){
      return `<a onclick="navigateTo('${currentUrl}#${titleId}')" class="fas fa-anchor"></a> ${fullMatch}`;
    }
    return value.replace(linkRegex, replacer);
  }

}
