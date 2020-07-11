import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class MobileMenuHelperService {

  private menuDisplayed = false;

  constructor() { }

  get isMobileMenuDisplayed() {
    return this.menuDisplayed;
  }

  toggleMobileMenu() {
    this.menuDisplayed = !this.menuDisplayed;
  }
}
