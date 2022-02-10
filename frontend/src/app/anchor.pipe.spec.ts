import { AnchorPipe } from './anchor.pipe';
import { ActivatedRouteStub } from './_testUtils/activated-route-stub.spec';
import { DomSanitizer } from '@angular/platform-browser';
import {TestBed} from "@angular/core/testing";

describe('AnchorPipe', () => {
  let pipe: AnchorPipe;
  let sanitizer: DomSanitizer;
  let activatedRoute;

  beforeEach(() => {
    activatedRoute = new ActivatedRouteStub() as any;
    activatedRoute.testParams = { nodes: '_Tools', project: 'theGardener', branch: 'master', directories: '_Guide_Write', page: 'Basics' };
    sanitizer = TestBed.inject(DomSanitizer);
    pipe = new AnchorPipe(activatedRoute);
  });

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('place an anchor before each title', () => {
    expect(pipe.transform(sanitizer.bypassSecurityTrustHtml(HTML_INPUT_WITH_SPACE_IN_TITLE))).toEqual(sanitizer.bypassSecurityTrustHtml(HTML_OUTPUT_WITH_SPACE_IN_TITLE));
    expect(pipe.transform(sanitizer.bypassSecurityTrustHtml(HTML_INPUT_WITHOUT_SPACE_IN_TITLE))).toEqual(sanitizer.bypassSecurityTrustHtml(HTML_OUTPUT_WITHOUT_SPACE_IN_TITLE));

    console.log(pipe.transform(sanitizer.bypassSecurityTrustHtml(COMPLEX_HTML_INPUT))['changingThisBreaksApplicationSecurity'])
    console.log(pipe.transform(sanitizer.bypassSecurityTrustHtml(COMPLEX_HTML_OUTPUT))['changingThisBreaksApplicationSecurity'])
    expect(pipe.transform(sanitizer.bypassSecurityTrustHtml(COMPLEX_HTML_INPUT))).toEqual(sanitizer.bypassSecurityTrustHtml(COMPLEX_HTML_OUTPUT));
  });
});

const HTML_INPUT_WITH_SPACE_IN_TITLE = '<h2 id="service-specification">Service specification</h2>';

const HTML_OUTPUT_WITH_SPACE_IN_TITLE =
  '<h2 id="service-specification">Service specification <a class="linkToAnchorForTitleAndSubTitle" onclick="navigateTo(\'app/documentation/navigate/_Tools/theGardener/master/_Guide_Write/Basics#service-specification\')"> <i class="fas fa-link"></i> </a> </h2>';

const HTML_INPUT_WITHOUT_SPACE_IN_TITLE = '<h1 id="requirement">Requirement</h1>';

const HTML_OUTPUT_WITHOUT_SPACE_IN_TITLE =
  '<h1 id="requirement">Requirement <a class="linkToAnchorForTitleAndSubTitle" onclick="navigateTo(\'app/documentation/navigate/_Tools/theGardener/master/_Guide_Write/Basics#requirement\')"> <i class="fas fa-link"></i> </a> </h1>';

const COMPLEX_HTML_INPUT =
  '<h2 id="service-specification">Service specification</h2> <a href="link"></a> <h1 id="requirement">Requirement</h1> <h3 id="development-on-back">Development on Back</h3>';

/* eslint-disable max-len */
const COMPLEX_HTML_OUTPUT =
  '<h2 id="service-specification">Service specification <a class="linkToAnchorForTitleAndSubTitle" onclick="navigateTo(\'app/documentation/navigate/_Tools/theGardener/master/_Guide_Write/Basics#service-specification\')"> <i class="fas fa-link"></i> </a> </h2> <a href="link"></a> <h1 id="requirement">Requirement <a class="linkToAnchorForTitleAndSubTitle" onclick="navigateTo(\'app/documentation/navigate/_Tools/theGardener/master/_Guide_Write/Basics#requirement\')"> <i class="fas fa-link"></i> </a> </h1> <h3 id="development-on-back">Development on Back <a class="linkToAnchorForTitleAndSubTitle" onclick="navigateTo(\'app/documentation/navigate/_Tools/theGardener/master/_Guide_Write/Basics#development-on-back\')"> <i class="fas fa-link"></i> </a> </h3>';
