import { Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'anchor'
})
export class AnchorPipe implements PipeTransform {
  constructor(private activatedRoute: ActivatedRoute) {}

  transform(safeHtml: SafeHtml): SafeHtml {
    const params = this.activatedRoute.snapshot.params;
    const linkRegexString = '<h(\\d) id="(\\S*)?">.*?<\\/h\\d>';
    const linkRegex = new RegExp(linkRegexString, 'g');
    const currentUrl = `app/documentation/navigate/${params['nodes']}/${params['project']}/${params['branch']}/${params['directories']}/${params['page']}`;

    function replacer(fullMatch: string, hNumber: string, titleId: string) {
      return `${fullMatch.replace(
        `</h${hNumber}>`,
        ''
      )} <a class="linkToAnchorForTitleAndSubTitle" onclick="navigateTo('${currentUrl}#${titleId}')"> <i class="fas fa-link"></i> </a> </h${hNumber}>`;
    }

    safeHtml['changingThisBreaksApplicationSecurity'] = safeHtml['changingThisBreaksApplicationSecurity'].replace(linkRegex, replacer);

    return safeHtml;
  }
}
