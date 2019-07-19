import {AfterViewChecked, Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NavigationItem} from '../../_models/navigation';
import {GherkinNode, GherkinNodeApi} from '../../_models/gherkin';
import {GherkinService} from '../../_services/gherkin.service';
import {GherkinComponent} from '../gherkin/gherkin.component';
import {ActivatedRoute} from '@angular/router';
import {NotificationService} from '../../_services/notification.service';

@Component({
  selector: 'app-navigate-content',
  templateUrl: './navigate-content.component.html',
  styleUrls: ['./navigate-content.component.scss']
})
export class NavigateContentComponent implements OnInit, AfterViewChecked {

  @Input() selection: NavigationItem;

  @Input() display = false;

  @Output() gherkinData: Array<GherkinNode>;

  @ViewChild(GherkinComponent, {static: true}) gherkin: GherkinComponent;

  showProgressBar = false;

  hash: string;
  needToGoToHash = false;

  constructor(private gherkinService: GherkinService,
              private route: ActivatedRoute,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.route.fragment.subscribe((hash: string) => {
      if (hash) {
        if (hash !== this.hash) {
          this.needToGoToHash = true;
          this.hash = hash;
        }
      }
    });
  }

  ngAfterViewChecked() {
    this.selectHash();
  }

  selectHash() {
    if (this.hash && this.needToGoToHash) {
      const cmp = document.getElementById(this.hash);
      if (cmp) {
        this.needToGoToHash = false;
        cmp.scrollIntoView();
      }
    }
  }

  generateGherkin(route: string) {
    if (route !== '') {
      this.showProgressBar = true;
      const cmp = document.getElementById('top');
      if (cmp) {
        cmp.scrollIntoView();
      }

      this.gherkinService.generateGherkin(`project=${route}`).subscribe(
        (result: GherkinNodeApi) => {
          this.gherkinData = this.gherkinService.decorate(result);
          if (this.gherkin) {
            this.gherkin.updateGeneratedGherkin(this.gherkinData);
          }
          this.showProgressBar = false;
        }, error => {
          this.notificationService.showError('Error while getting this feature', error);
          this.showProgressBar = false;
        });

    }
  }

}
