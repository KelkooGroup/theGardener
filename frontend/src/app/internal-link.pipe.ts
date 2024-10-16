import { Pipe, PipeTransform } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RouteService } from './_services/route.service';
import { SafeHtml } from '@angular/platform-browser';

@Pipe({
  name: 'internalLink'
})
export class InternalLinkPipe implements PipeTransform {
  nodes: string;
  project: string;
  branch: string;
  directories: string;

  constructor(private activatedRoute: ActivatedRoute, private routeService: RouteService) {}

  transform(value: SafeHtml): SafeHtml {
    this.nodes = this.activatedRoute.snapshot.params['nodes'];
    this.project = this.activatedRoute.snapshot.params['project'];
    this.branch = this.activatedRoute.snapshot.params['branch'];
    this.directories = this.activatedRoute.snapshot.params['directories'];

    let transformedValue = this.transformLegacy(value['changingThisBreaksApplicationSecurity']);
    transformedValue = this.transformInternalLinks(transformedValue);
    transformedValue = this.transformInternalRelativeLinks(transformedValue);
    transformedValue = this.transformInternalRelativeLinksWithAnchor(transformedValue);

    value['changingThisBreaksApplicationSecurity'] = transformedValue;

    return value;
  }

  transformInternalRelativeLinks(value: string): string {
    const linkRegexString = '(href=)["\'](.*?)[.]md["\']';
    const linkRegex = new RegExp(linkRegexString, 'g');

    const nodes = this.nodes;
    const project = this.project;
    const branch = this.branch;
    const directories = this.directories;
    const routeService = this.routeService;

    function replacer(_p1: string, _p2: string, relativePath: string) {
      const targetUrl = routeService.relativeUrlToFullFrontEndUrl(relativePath, { nodes, project, branch, directories });
      return `onclick="navigateTo('${targetUrl}')"`;
    }

    return value.replace(linkRegex, replacer);
  }

  transformInternalRelativeLinksWithAnchor(value: string): string {
    const linkRegexString = '(href=)["\'](.*?)[.]md#([a-z-]+)["\']';
    const linkRegex = new RegExp(linkRegexString, 'g');

    const nodes = this.nodes;
    const project = this.project;
    const branch = this.branch;
    const directories = this.directories;
    const routeService = this.routeService;

    function replacer(_p1: string, _p2: string, relativePath: string, anchor: string) {
      const relativePathWithAnchor = `${relativePath}#${anchor}`;
      const targetUrl = routeService.relativeUrlToFullFrontEndUrl(relativePathWithAnchor, { nodes, project, branch, directories });
      return `onclick="navigateTo('${targetUrl}')"`;
    }

    return value.replace(linkRegex, replacer);
  }

  transformInternalLinks(value: string): string {
    const linkRegexString = '(href=)["\'](thegardener:\\/\\/navigate\\/)(.*?)["\']';
    const linkRegex = new RegExp(linkRegexString, 'g');

    function replacer(_p1: string, _p2: string, _p3: string, navigationPath: string) {
      const targetUrl = `app/documentation/navigate/${navigationPath}`;
      return `onclick="navigateTo('${targetUrl}')"`;
    }

    return value.replace(linkRegex, replacer);
  }

  transformLegacy(value: string): string {
    const linkRegexString = '(href=)["\'](thegardener:\\/\\/)(\\w*)?\\/?(\\w*)?;?path=(.*?)["\']';
    const linkRegex = new RegExp(linkRegexString, 'g');
    const nodes = this.nodes;

    function replacer(_p1: string, _p2: string, _p3: string, firstString: string, hierarchyIdGiven: string, path: string) {
      const targetLegacyUrl = `app/documentation/navigate/${firstString !== 'navigate' ? nodes : hierarchyIdGiven};path=${path}`;
      const targetUrl = RouteService.legacyFullFrontEndUrlToFullFrontEndUrl(targetLegacyUrl);
      return `onclick="navigateTo('${targetUrl}')"`;
    }

    return value.replace(linkRegex, replacer);
  }
}
