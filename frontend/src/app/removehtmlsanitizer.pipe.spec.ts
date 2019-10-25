import { RemoveHtmlSanitizerPipe } from './removehtmlsanitizer.pipe';
import {DomSanitizer} from "@angular/platform-browser";
import {TestBed} from "@angular/core/testing";

describe('RemovehtmlsanitizerPipe', () => {
  it('create an instance', () => {
    const sanitizer: DomSanitizer = TestBed.get(DomSanitizer);
    const pipe = new RemoveHtmlSanitizerPipe(sanitizer);
    expect(pipe).toBeTruthy();
  });
});
