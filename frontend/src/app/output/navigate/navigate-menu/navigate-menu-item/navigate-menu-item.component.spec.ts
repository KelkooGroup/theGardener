import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NavigateMenuItemComponent} from './navigate-menu-item.component';
import {MatFormFieldModule, MatIconModule, MatSelectModule, MatSnackBarModule} from '@angular/material';
import {RouterTestingModule} from '@angular/router/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub} from '../../../../test/activated-route-stub.spec';
import {MenuDirectoryHierarchy, MenuProjectHierarchy} from '../../../../_models/menu';
import {FormsModule} from '@angular/forms';

describe('NavigateMenuItemComponent', () => {
  let component: NavigateMenuItemComponent;
  let fixture: ComponentFixture<NavigateMenuItemComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavigateMenuItemComponent],
      imports: [
        MatIconModule,
        MatFormFieldModule,
        MatSelectModule,
        MatSnackBarModule,
        FormsModule,
        RouterTestingModule,
        NoopAnimationsModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useClass: ActivatedRouteStub
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigateMenuItemComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = fixture.debugElement.injector.get(ActivatedRoute) as any;
  });

  it('should create without chevron when no children', () => {
    activatedRoute.testParams = {};
    component.menuItem = {
      name: 'suggestion',
      label: 'Suggestion system',
      type: 'Node',
      depth: 1,
      children: []
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(page.label.textContent).toEqual('Suggestion system');
    expect(page.chevron).toBeFalsy();
  });

  it('should expand / collapse hierarchy nodes', () => {
    activatedRoute.testParams = {};
    component.menuItem = {
      name: 'suggestion',
      label: 'Suggestion system',
      type: 'Node',
      depth: 1,
      children: [
        {
          name: 'child1',
          label: 'Child 1',
          type: 'Node',
          depth: 2,
          children: []
        },
        {
          name: 'child2',
          label: 'Child 2',
          type: 'Node',
          depth: 2,
          children: []
        },
      ]
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(page.label.textContent).toEqual('Suggestion system');
    expect(component.expanded).toBeTruthy();
    page.clickOnChevron();
    expect(component.expanded).toBeFalsy();
  });

  it('should activate node if path is current route', () => {
    activatedRoute.testParams = {name: 'library'};
    component.menuItem = {
      name: 'suggestion',
      label: 'Suggestion system',
      type: 'Node',
      depth: 1,
      route: 'suggestion',
      children: [
        {
          name: 'branch',
          label: 'Child 1',
          type: 'Node',
          depth: 2,
          children: []
        },
      ]
    };
    fixture.detectChanges();
    expect(component.nodeNameInUrl).toEqual('library');

    const router: Router = TestBed.get(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    page.clickOnItem();
    expect(router.navigate).toHaveBeenCalledWith(['app/documentation/navigate/library', {path: 'suggestion'}]);
  });

  it('should show branches in a select if node is a project', () => {
    activatedRoute.testParams = {name: 'eng', path: 'suggestionWS'};
    const projectItem: MenuProjectHierarchy = {
      name: 'suggestionWS',
      label: 'Suggestion Webservice',
      type: 'Project',
      stableBranch: 'qa',
      depth: 2,
      route: 'suggestionWS',
      children: [
        {
          name: 'qa',
          label: 'qa',
          type: 'Branch',
          depth: 3,
          children: []
        },
        {
          name: 'branch1',
          label: 'Branch 1',
          type: 'Branch',
          depth: 3,
          children: []
        },
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    expect(page.branchSelect).toBeTruthy();
    expect(component.selectedBranch).toBeTruthy();
    expect(component.selectedBranch.name).toEqual(projectItem.stableBranch);
  });

  it('should show not branches select if only one branch', () => {
    activatedRoute.testParams = {name: 'eng', path: 'suggestionWS'};
    const projectItem: MenuProjectHierarchy = {
      name: 'suggestionWS',
      label: 'Suggestion Webservice',
      type: 'Project',
      stableBranch: 'qa',
      depth: 2,
      route: 'suggestionWS',
      children: [
        {
          name: 'qa',
          label: 'qa',
          type: 'Branch',
          depth: 3,
          children: [{
            name: 'suggestionWS',
            label: 'suggestionWS',
            type: 'Directory',
            route: 'suggestionsWS>qa>/',
            depth: 4,
            children: [],
          }]
        },
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    expect(page.branchSelect).toBeFalsy();
  });

  it('should show directory for selected branch if node is a project', async(() => {
    activatedRoute.testParams = {name: 'eng', path: 'suggestionWS'};
    const rootDirectoryItem: MenuDirectoryHierarchy = {
      name: 'suggestionWS',
      label: 'suggestionWS',
      type: 'Directory',
      description: 'Suggestion WS documentation',
      route: 'suggestionsWS>qa>/',
      depth: 4,
      order: 0,
      children: [],
    };

    const projectItem: MenuProjectHierarchy = {
      name: 'suggestionWS',
      label: 'Suggestion Webservice',
      type: 'Project',
      stableBranch: 'qa',
      depth: 2,
      route: 'suggestionWS',
      children: [
        {
          name: 'qa',
          label: 'qa',
          type: 'Branch',
          depth: 3,
          children: [
            rootDirectoryItem,
          ]
        },
        {
          name: 'branch1',
          label: 'Branch 1',
          type: 'Branch',
          depth: 3,
          children: []
        },
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    // default branch is selected. Check that associated directory is displayed.
    expect(page.directories).toBeTruthy();
    expect(page.directories.length).toEqual(1);
  }));

  it('should not fail if selected branch has no root directory', () => {
    activatedRoute.testParams = {name: 'eng', path: 'suggestionWS'};

    const projectItem: MenuProjectHierarchy = {
      name: 'suggestionWS',
      label: 'Suggestion Webservice',
      type: 'Project',
      stableBranch: 'qa',
      depth: 2,
      route: 'suggestionWS',
      children: [
        {
          name: 'qa',
          label: 'qa',
          type: 'Branch',
          depth: 3,
          children: []
        },
        {
          name: 'branch1',
          label: 'Branch 1',
          type: 'Branch',
          depth: 3,
          children: []
        },
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    expect(page.directories.length).toBe(0);

  });
});

class Page {
  constructor(private fixture: ComponentFixture<NavigateMenuItemComponent>) {
  }

  get label(): HTMLElement {
    return this.fixture.nativeElement.querySelector('.item-text');
  }

  get chevron(): HTMLElement {
    return this.fixture.nativeElement.querySelector('mat-icon');
  }

  get branchSelect(): HTMLElement {
    return this.fixture.nativeElement.querySelector('mat-select');
  }

  get directories(): Array<HTMLElement> {
    return this.fixture.nativeElement.querySelectorAll('app-navigate-menu-item');
  }

  clickOnChevron() {
    this.chevron.click();
    this.fixture.detectChanges();
  }

  clickOnItem() {
    this.label.click();
    this.fixture.detectChanges();
  }
}
