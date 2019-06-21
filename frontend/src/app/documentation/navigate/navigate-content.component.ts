import {AfterViewChecked, Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NavigationItem} from '../../_models/navigation';
import {DocumentationNode, DocumentationNodeApi} from '../../_models/documentation';
import {DocumentationService} from '../../_services/documentation.service';
import {DocumentationThemeBookComponent} from '../themes/documentation-theme-book/documentation-theme-book.component';
import {ActivatedRoute} from '@angular/router';
import {NotificationService} from "../../_services/notification.service";

@Component({
  selector: 'app-navigate-content',
  templateUrl: './navigate-content.component.html',
  styleUrls: ['./navigate-content.component.scss']
})
export class NavigateContentComponent implements OnInit, AfterViewChecked {

  @Input() selection: NavigationItem;

  @Input() display = false;

  @Output() documentationData: Array<DocumentationNode>;

  @ViewChild(DocumentationThemeBookComponent, {static: true}) documentationTheme: DocumentationThemeBookComponent;

  showProgressBar = false;

  hash: string;
  needToGoToHash = false;

  constructor(private documentationService: DocumentationService, private route: ActivatedRoute, private notificationService: NotificationService) {
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

  generateDocumentation(route: string) {
    if (route !== '') {
      this.showProgressBar = true;
      const cmp = document.getElementById('top');
      if (cmp) {
        cmp.scrollIntoView();
      }

      this.documentationService.generateDocumentation(`project=${route}`).subscribe(
        (result: DocumentationNodeApi) => {
          this.documentationData = this.documentationService.decorate(result);
          if (this.documentationTheme) {
            this.documentationTheme.updateGeneratedDocumentation(this.documentationData);
          }
        },
        (error) => {
          this.notificationService.showError("Couldn't access this feature",error);
        });

    }
  }

  hideProgressBar() {
    this.showProgressBar = false;
  }

}
