import {AfterViewChecked, Component, Input, OnInit, Output, ViewChild} from '@angular/core';
import {NavigationItem} from '../../_models/navigation';
import {DocumentationNode, DocumentationNodeApi} from '../../_models/documentation';
import {DocumentationService} from '../../_services/documentation.service';
import {DocumentationThemeBookComponent} from '../themes/documentation-theme-book/documentation-theme-book.component';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-navigate-content',
  templateUrl: './navigate-content.component.html',
  styleUrls: ['./navigate-content.component.scss']
})
export class NavigateContentComponent implements OnInit, AfterViewChecked {

  @Input() selection: NavigationItem;

  @Input() display = false;

  @Output() documentationData: Array<DocumentationNode>;

  @ViewChild(DocumentationThemeBookComponent) documentationTheme: DocumentationThemeBookComponent;

  showProgressBar = false;

  hash: string;
  needToGoToHash = false;

  constructor(private documentationService: DocumentationService, private route: ActivatedRoute) {
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
          return new Promise(() => {
            setTimeout(() => {
              this.selectHash();
            }, 1000);
          });
        },
        () => {
        });

    }
  }

  hideProgressBar() {
    return new Promise(() => {
      setTimeout(() => {
        this.showProgressBar = false;
      }, 10);
    });

  }

}
