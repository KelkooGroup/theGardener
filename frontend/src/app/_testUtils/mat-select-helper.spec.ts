/* eslint-disable @typescript-eslint/naming-convention, no-underscore-dangle, id-blacklist, id-match */
import { OverlayContainer } from '@angular/cdk/overlay';
import { ComponentFixture, inject } from '@angular/core/testing';

// Source: https://gist.github.com/glendaviesnz/fc8e99b41f0dda8b1c0dc4d397e0d152
export class MatSelectHelper {
  private _container: OverlayContainer;
  private _containerElement: HTMLElement;
  private _trigger: HTMLElement;

  constructor(private _fixture: ComponentFixture<any>, private _selectId: string = '') {
    inject([OverlayContainer], (oc: OverlayContainer) => {
      this._container = oc;
      this._containerElement = oc.getContainerElement();
    })();
  }

  triggerMenu() {
    this._fixture.detectChanges();
    this._trigger = this._fixture.nativeElement.querySelector(this._selectId + ' .mat-select-trigger');
    this._trigger.click();
    this._fixture.detectChanges();
  }

  getOptions(): Array<HTMLElement> {
    return Array.from(this._containerElement.querySelectorAll('mat-option') as NodeListOf<HTMLElement>);
  }

  selectOption(option: HTMLElement): Promise<any> {
    option.click();
    this._fixture.detectChanges();
    this._trigger.click();
    this._fixture.detectChanges();
    return this._fixture.whenStable();
  }

  selectOptionByKey(key: string): Promise<any> {
    const option = this.getOptions().find((o: HTMLElement) => o.innerText.trim() === key);
    return this.selectOption(option);
  }

  cleanup() {
    this._container.ngOnDestroy();
  }
}
