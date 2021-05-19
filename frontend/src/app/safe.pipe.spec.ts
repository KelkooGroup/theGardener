import { SafePipe } from './safe.pipe';
import { TestBed } from '@angular/core/testing';
import { DomSanitizer } from '@angular/platform-browser';

describe('SafePipe', () => {
  it('create an instance', () => {
    const sanitizer: DomSanitizer = TestBed.inject(DomSanitizer);
    const pipe = new SafePipe(sanitizer);
    expect(pipe).toBeTruthy();
  });
});
