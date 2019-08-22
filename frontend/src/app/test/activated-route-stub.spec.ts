import {convertToParamMap, ParamMap, UrlSegment} from '@angular/router';
import {BehaviorSubject} from 'rxjs';
import {Injectable} from '@angular/core';

/**
 * An ActivateRoute test double with a `paramMap` observable.
 * Use the `setParamMap()` method to add the next `paramMap` value.
 */
@Injectable()
export class ActivatedRouteStub {
  // Observable that contains a map of the parameters
  private subjectParamMap = new BehaviorSubject(convertToParamMap(this.testParamMap));
  private subjectParams = new BehaviorSubject(this.testParams);
  private subjectParentParamMap = new BehaviorSubject(convertToParamMap(this.testParentParamMap));
  private subjectParentParams = new BehaviorSubject(this.testParentParams);
  private subjectUrl = new BehaviorSubject(this.testUrl);

  paramMap = this.subjectParamMap.asObservable();

  private _testParamMap: ParamMap;
  get testParamMap() {
    return this._testParamMap;
  }
  set testParamMap(params: {}) {
    this._testParamMap = convertToParamMap(params);
    this.subjectParamMap.next(this._testParamMap);
  }

  private _testParams: Object;
  get testParams() {
    return this._testParams;
  }
  set testParams(params) {
    this._testParams = params;
    this.subjectParams.next(this._testParams);
  }

  private _testParentParamMap: ParamMap;
  get testParentParamMap() {
    return this._testParentParamMap;
  }
  set testParentParamMap(params: {}) {
    this._testParentParamMap = convertToParamMap(params);
    this.subjectParentParamMap.next(this._testParentParamMap);
  }

  private _testParentParams: Object;
  get testParentParams() {
    return this._testParentParams;
  }
  set testParentParams(params) {
    this._testParentParams = params;
    this.subjectParentParams.next(this._testParentParams);
  }

  private _testUrl: UrlSegment[];
  get testUrl() {
    return this._testUrl;
  }
  set testUrl(urlSegments: UrlSegment[]) {
    this._testUrl = urlSegments;
    this.subjectUrl.next(this._testUrl);
  }

  // Observable that contains a map of the query parameters
  private subjectQueryParamMap = new BehaviorSubject(convertToParamMap(this.testParamMap));
  queryParamMap = this.subjectQueryParamMap.asObservable();

  private _testQueryParamMap: ParamMap;
  get testQueryParamMap() {
    return this._testQueryParamMap;
  }

  set testQueryParamMap(params: {}) {
    this._testQueryParamMap = convertToParamMap(params);
    this.subjectQueryParamMap.next(this._testQueryParamMap);
  }

  get params() {
    return this.subjectParams.asObservable();
  }

  get snapshot() {
    return {
      params: this.testParams,
      paramMap: this.testParamMap,
      queryParamMap: this.testQueryParamMap,
      url: this.testUrl,
    };
  }

  get parent() {
    return {
      snapshot: {
        paramMap: this.testParentParamMap,
        params: this.testParentParams,
      },
      paramMap: this.subjectParentParamMap.asObservable(),
      params: this.subjectParentParams.asObservable(),
    };
  }

}
