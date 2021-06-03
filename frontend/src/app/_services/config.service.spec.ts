import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed, waitForAsync } from '@angular/core/testing';

import { ConfigService } from './config.service';
import { Config } from '../_models/config';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('ConfigService', () => {
  let httpMock: HttpTestingController;
  let configService: ConfigService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, MatSnackBarModule],
        providers: [ConfigService]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    httpMock = TestBed.inject(HttpTestingController);
    configService = TestBed.inject(ConfigService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    const service: ConfigService = TestBed.inject(ConfigService);
    expect(service).toBeTruthy();
  });

  it(
    'should parse service response',
    waitForAsync(() => {
      configService.load().then(h => {
        expect(h.windowTitle).toBe('theGardener');
        expect(h.title).toBe('In our documentation we trust.');
        expect(h.logoSrc).toBe('assets/images/logo-white.png');
        expect(h.faviconSrc).toBe('assets/images/favicon.png');
        expect(h.colorMain).toBe('#1F7079');
        expect(h.colorDark).toBe('#0f3438');
        expect(h.colorLight).toBe('#b4ced1');
        expect(h.translateTo).toBe('fr,de,es');
        expect(h.translateTemplate).toBe('https://translate.google.com/translate?hl=en&sl=auto&tl={{to}}&u={{encoded_url}}');
      });
      const req = httpMock.match('api/config')[0];
      expect(req.request.method).toBe('GET');
      req.flush(SERVER_RESPONSE);
    })
  );
});

const SERVER_RESPONSE: Config = {
  windowTitle: 'theGardener',
  title: 'In our documentation we trust.',
  logoSrc: 'assets/images/logo-white.png',
  faviconSrc: 'assets/images/favicon.png',
  baseUrl: 'http://localhost:9000',
  colorMain: '#1F7079',
  colorDark: '#0f3438',
  colorLight: '#b4ced1',
  translateTo: 'fr,de,es',
  translateTemplate: 'https://translate.google.com/translate?hl=en&sl=auto&tl={{to}}&u={{encoded_url}}'
};
