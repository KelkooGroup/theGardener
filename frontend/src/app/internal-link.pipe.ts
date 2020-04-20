import {Pipe, PipeTransform} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {RouteService} from "./_services/route.service";

@Pipe({
  name: 'internalLink'
})
export class InternalLinkPipe implements PipeTransform {

  nodes : string;
  project : string;
  branch : string;
  directories : string;


  constructor(private activatedRoute: ActivatedRoute) {
  }

  transform(value: string): string {
    this.nodes = this.activatedRoute.snapshot.params.nodes;
    this.project = this.activatedRoute.snapshot.params.project;
    this.branch = this.activatedRoute.snapshot.params.branch;
    this.directories = this.activatedRoute.snapshot.params.directories;

    let transformedValue = this.transformLegacy(value);
    transformedValue = this.transformInternalLinks(transformedValue);
    transformedValue = this.transformInternalRelativeLinks(transformedValue);
    return transformedValue;
  }

  transformInternalRelativeLinks(value: string): string {
    const linkRegexString = '(href=)["\'](.*?)[\.]md["\']';
    const linkRegex = new RegExp(linkRegexString, 'g');

    const nodes = this.nodes;
    const project = this.project;
    const branch = this.branch;
    const directories = this.directories;

    function replacer(p1: string, p2: string, relativePath: string) {
      const targetUrl = RouteService.relativeUrlToFullFrontEndUrl(relativePath, {nodes,project,branch,directories});
      return `onclick="navigateTo('${targetUrl}')"`;
    }

    return value.replace(linkRegex, replacer);
  }

  transformInternalLinks(value: string): string {
    const linkRegexString = '(href=)["\'](thegardener:\\/\\/navigate\\/)(.*?)["\']';
    const linkRegex = new RegExp(linkRegexString, 'g');

    function replacer(p1: string, p2: string, p3: string, navigationPath: string) {
      const targetUrl = `app/documentation/navigate/${navigationPath}`;
      return `onclick="navigateTo('${targetUrl}')"`;
    }

    return value.replace(linkRegex, replacer);
  }

  transformLegacy(value: string): string {
    const linkRegexString = '(href=)["\'](thegardener:\\/\\/)(\\w*)?\\/?(\\w*)?;?path=(.*?)["\']';
    const linkRegex = new RegExp(linkRegexString, 'g');
    const nodes = this.nodes;

    function replacer(p1: string, p2: string, p3: string, firstString: string, hierarchyIdGiven: string, path: string) {
      const targetLegacyUrl = `app/documentation/navigate/${firstString !== 'navigate' ? nodes : hierarchyIdGiven};path=${path}`;
      const targetUrl = RouteService.legacyFullFrontEndUrlToFullFrontEndUrl(targetLegacyUrl);
      return `onclick="navigateTo('${targetUrl}')"`;
    }

    return value.replace(linkRegex, replacer);
  }


}

