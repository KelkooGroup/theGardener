import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PageContentComponent} from './page-content.component';
import {ActivatedRoute} from '@angular/router';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ActivatedRouteStub} from '../../test/activated-route-stub.spec';
import {PageService} from '../../_services/page.service';
import {PAGE_SERVICE_RESPONSE} from '../../test/test-data.spec';
import {of} from 'rxjs';

describe('PageContentComponent', () => {
  let component: PageContentComponent;
  let fixture: ComponentFixture<PageContentComponent>;
  let activatedRoute: ActivatedRouteStub;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        PageContentComponent
      ],
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useClass: ActivatedRouteStub,
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PageContentComponent);
    component = fixture.componentInstance;
    activatedRoute = fixture.debugElement.injector.get(ActivatedRoute) as any;
  });

  it('should get page content from backend', async(() => {
    const pageService: PageService = TestBed.get(PageService);
    spyOn(pageService, 'getPage').and.returnValue(of(PAGE_SERVICE_RESPONSE));

    activatedRoute.testParentParams = {name: '_eng', path: 'suggestionsWS>qa>/pmws/'};
    activatedRoute.testParams = {page: 'overview'};
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(pageService.getPage).toHaveBeenCalledWith('suggestionsWS>qa>/pmws/overview')
  }));
});
