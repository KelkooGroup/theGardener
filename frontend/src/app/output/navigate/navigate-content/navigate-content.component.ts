import {Component} from '@angular/core';
import {MobileMenuHelperService} from '../../../_services/mobile-menu-helper.service';

@Component({
  selector: 'app-navigate-content',
  templateUrl: './navigate-content.component.html',
  styleUrls: ['./navigate-content.component.scss']
})
export class NavigateContentComponent  {

  constructor(public mobileMenuService: MobileMenuHelperService) {
  }
}
