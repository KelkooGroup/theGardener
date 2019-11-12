import {RemoveHtmlSanitizerPipe} from './removehtmlsanitizer.pipe';
import {DomSanitizer} from '@angular/platform-browser';
import {TestBed} from '@angular/core/testing';

describe('RemovehtmlsanitizerPipe', () => {
  let sanitizer: DomSanitizer;
  let removeSanitizerPipe: RemoveHtmlSanitizerPipe;

  beforeEach(() => {
    sanitizer = TestBed.get(DomSanitizer);
    removeSanitizerPipe = new RemoveHtmlSanitizerPipe(sanitizer);
  });

  it('create an instance', () => {
    expect(removeSanitizerPipe).toBeTruthy();
  });

  it('Remove sanitizer', () => {
    expect(sanitizer.sanitize(1, removeSanitizerPipe.transform(htmlTest))).toBe(htmlTest);
  });

  const htmlTest = '<p>For instance :\n<a onclick="navigateTo(\'app/documentation/navigate/_tools;path=theGardener%3Emaster%3E_guides_/install\')">internal link to installation guide of the project theGardener</a></p>';
});
