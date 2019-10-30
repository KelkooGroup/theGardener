import {Pipe, PipeTransform} from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Pipe({
  name: 'internalLink'
})
export class InternalLinkPipe implements PipeTransform {

  constructor(private activatedRoute: ActivatedRoute) {
  }


  transform(value: string): string {
    const linkRegexString = '(href=)["\'](thegardener:\\/\\/)(\\w*)?\\/?(\\w*)?;?path=(.*?)["\']';
    const linkRegex = new RegExp(linkRegexString, 'g');
    const hierarchy = this.activatedRoute.parent.snapshot.params.name;

    function replacer(p1: string, p2: string, p3: string, p4: string, p5: string, p6: string) {
      return `onclick="navigateTo('app/documentation/navigate/${p4 !== 'navigate' ? hierarchy : p5};path=${p6}')"`;
    }

    return value.replace(linkRegex, replacer);
  }


}

