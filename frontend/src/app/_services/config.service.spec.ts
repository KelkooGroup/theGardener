import {HttpClientTestingModule, HttpTestingController} from '@angular/common/http/testing';
import {async, TestBed} from '@angular/core/testing';

import {ConfigService} from './config.service';
import {Config} from '../_models/config';

describe('ConfigService', () => {
  let httpMock: HttpTestingController;
  let configService: ConfigService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ConfigService]
    }).compileComponents();
  }));

  beforeEach(() => {
    httpMock = TestBed.get(HttpTestingController);
    configService = TestBed.get(ConfigService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    const service: ConfigService = TestBed.get(ConfigService);
    expect(service).toBeTruthy();
    httpMock.expectOne('api/config');
  });

  it('should parse service response', async(() => {
    configService.getConfigs().subscribe(h => {
      expect(h.windowTitle).toBe('theGardener');
      expect(h.title).toBe('In our documentation we trust.');
      expect(h.logoSrc).toBe('assets/images/logo-white.png');
      expect(h.faviconSrc).toBe('assets/images/favicon.png');
      expect(h.baseUrl).toBe('http://localhost:9000');
      expect(h.colorMain).toBe('#1F7079');
      expect(h.colorDark).toBe('#0f3438');
      expect(h.colorLight).toBe('#b4ced1');
    });
    const req = httpMock.match('api/config')[0];
    expect(req.request.method).toBe('GET');
    req.flush(SERVER_RESPONSE);
  }));
});

const SERVER_RESPONSE: Config = {
  windowTitle: 'theGardener',
  title: 'In our documentation we trust.',
  logoSrc: 'assets/images/logo-white.png',
  faviconSrc: 'assets/images/favicon.png',
  baseUrl:"http://localhost:9000",
  colorMain:"#1F7079",
  colorDark:"#0f3438",
  colorLight:"#b4ced1"
};
