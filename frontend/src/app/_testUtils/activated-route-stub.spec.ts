import {convertToParamMap, ParamMap, UrlSegment} from '@angular/router';
import {BehaviorSubject} from 'rxjs';
import {Injectable} from '@angular/core';

/**
 * An ActivateRoute test double with a `paramMap` observable.
 * Use the `setParamMap()` method to add the next `paramMap` value.
 */
@Injectable()
// tslint:disable:variable-name
export class ActivatedRouteStub {
  // Observable that contains a map of the parameters
  private subjectParamMap = new BehaviorSubject(convertToParamMap(this.testParamMap));
  private subjectParams = new BehaviorSubject(this.testParams);
  private subjectParentParamMap = new BehaviorSubject(convertToParamMap(this.testParentParamMap));
  private subjectParentParams = new BehaviorSubject(this.testParentParams);
  private subjectChildParamMap = new BehaviorSubject(convertToParamMap(this.testChildParamMap));
  private subjectChildParams = new BehaviorSubject(this.testChildParams);
  private subjectChildUrl = new BehaviorSubject(this.testChildUrl);
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

  private _testParams: object = {};
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

  private _testParentParams: object = {};
  get testParentParams() {
    return this._testParentParams;
  }

  set testParentParams(params) {
    this._testParentParams = params;
    this.subjectParentParams.next(this._testParentParams);
  }

  private _testChildParamMap: ParamMap;
  get testChildParamMap() {
    return this._testChildParamMap;
  }

  set testChildParamMap(params: {}) {
    this._testChildParamMap = convertToParamMap(params);
    this.subjectChildParamMap.next(this._testChildParamMap);
  }

  private _testChildParams: object = {};
  get testChildParams() {
    return this._testChildParams;
  }

  set testChildParams(params) {
    this._testChildParams = params;
    this.subjectChildParams.next(this._testChildParams);
  }

  private _testChildUrl: Array<UrlSegment>;
  get testChildUrl() {
    return this._testChildUrl;
  }

  set testChildUrl(childUrl: Array<UrlSegment>) {
    this._testChildUrl = childUrl;
    this.subjectChildUrl.next(this._testChildUrl);
  }


  private _testUrl: Array<UrlSegment>;
  get testUrl() {
    return this._testUrl;
  }

  set testUrl(urlSegments: Array<UrlSegment>) {
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

  get firstChild() {
    return {
      snapshot: {
        paramMap: this.testChildParamMap,
        params: this.testChildParams,
        url: this.testChildUrl,
      },
      paramMap: this.subjectChildParamMap.asObservable(),
      params: this.subjectChildParams.asObservable(),
      url: this.subjectChildUrl.asObservable(),
    };
  }

  private _fragment: object = {};
  get fragment() {
    return this._fragment
  }

  set fragment(params) {
    this._fragment = params;
  }

}
