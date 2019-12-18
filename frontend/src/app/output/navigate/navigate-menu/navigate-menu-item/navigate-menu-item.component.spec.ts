import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NavigateMenuItemComponent} from './navigate-menu-item.component';
import {MatFormFieldModule, MatIconModule, MatSelectModule, MatSnackBarModule} from '@angular/material';
import {RouterTestingModule} from '@angular/router/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub} from '../../../../_testUtils/activated-route-stub.spec';
import {MenuDirectoryHierarchy, MenuProjectHierarchy} from '../../../../_models/menu';
import {FormsModule} from '@angular/forms';
import {MatSelectHelper} from '../../../../_testUtils/mat-select-helper.spec';

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

  it('should navigate to item on click', () => {
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

  it('should activate node if path is current route with no branch name from internal link',() => {
    activatedRoute.testParams = {name: 'library'};
    component.menuItem = {
      name: 'theGardener',
      label: 'Suggestion system',
      type: 'Node',
      depth: 1,
      route: 'theGardener>>_/theTeaser',
      children: [
        {
          name: 'master',
          label: 'Child 1',
          type: 'Node',
          depth: 2,
          children: []
        },
      ]
    };
    fixture.detectChanges();
    component.pathInUrl = 'theGardener>master>_/theTeaser';
    fixture.detectChanges();
    expect(component.isNodeActive(component.menuItem)).toBeTruthy();
  });

  it('should activate node if path is current route with no branch name',() => {
    activatedRoute.testParams = {name: 'library'};
    component.menuItem = {
      name: 'theGardener',
      label: 'Suggestion system',
      type: 'Node',
      depth: 1,
      route: 'theGardener>>_/theTeaser',
      children: [
        {
          name: 'master',
          label: 'Child 1',
          type: 'Node',
          depth: 2,
          children: []
        },
      ]
    };
    fixture.detectChanges();
    component.pathInUrl = 'theGardener>>_/theTeaser';
    fixture.detectChanges();
    expect(component.isNodeActive(component.menuItem)).toBeTruthy();
  });

  it('should show branches in a select if node is a project', async(() => {
    activatedRoute.testParams = {name: '_eng', path: 'suggestionWS'};
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

    expect(component.selectedBranch).toBeTruthy();
    expect(component.selectedBranch.name).toEqual(projectItem.stableBranch);
    expect(page.branchSelect).toBeTruthy();
    expect(page.availableBranches.length).toBe(2);
    expect(page.availableBranches[0].textContent).toMatch('qa');
    expect(page.availableBranches[1].textContent).toMatch('branch1');
  }));

  it('should select stable branch by default', () => {
    activatedRoute.testParams = {name: '_eng', path: 'suggestionWS'};
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

    expect(component.selectedBranch.name).toEqual(projectItem.stableBranch);
  });

  it('should select branch specified in URL', () => {
    activatedRoute.testParams = {name: '_eng', path: 'suggestionWS>branch1>'};
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

    expect(component.selectedBranch.name).toEqual('branch1');
  });

  it('should navigate to the right URL when selecting a branch', () => {
    activatedRoute.testParams = {name: '_eng', path: 'suggestionWS'};
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
          route: 'suggestionWS>qa>/',
          children: []
        },
        {
          name: 'branch1',
          label: 'Branch 1',
          type: 'Branch',
          depth: 3,
          route: 'suggestionWS>branch1>/',
          children: []
        },
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    const router: Router = TestBed.get(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    // when
    page.selectBranch('branch1');

    // then
    expect(router.navigate).toHaveBeenCalledWith(['app/documentation/navigate/_eng', {path: 'suggestionWS>branch1>/'}]);
  });

  it('should navigate to the right URL when selecting a branch with / or _ in the name', () => {
    activatedRoute.testParams = {name: '_eng', path: 'suggestionWS'};
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
          route: 'suggestionWS>qa>/',
          children: []
        },
        {
          name: 'foo/bar_foo_bar',
          label: 'Branch 1',
          type: 'Branch',
          depth: 3,
          route: 'suggestionWS>foo_bar~foo~bar>/',
          children: []
        },
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    const router: Router = TestBed.get(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    // when
    page.selectBranch('foo/bar_foo_bar');

    // then
    expect(router.navigate).toHaveBeenCalledWith(['app/documentation/navigate/_eng', {path: 'suggestionWS>foo_bar~foo~bar>/'}]);
  });

  it('should not show branches select if only one branch', () => {
    activatedRoute.testParams = {name: '_eng', path: 'suggestionWS'};
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
    activatedRoute.testParams = {name: '_eng', path: 'suggestionWS'};
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
    activatedRoute.testParams = {name: '_eng', path: 'suggestionWS'};

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
  private select: MatSelectHelper;

  constructor(private fixture: ComponentFixture<NavigateMenuItemComponent>) {
    this.select = new MatSelectHelper(this.fixture, '#branchSelect');
  }

  get label(): HTMLElement {
    return this.fixture.nativeElement.querySelector('.item-text');
  }

  get chevron(): HTMLElement {
    return this.fixture.nativeElement.querySelector('#expandIcon');
  }

  get branchSelect(): HTMLElement {
    return this.fixture.nativeElement.querySelector('mat-select');
  }

  get directories(): Array<HTMLElement> {
    return this.fixture.nativeElement.querySelectorAll('app-navigate-menu-item');
  }

  get availableBranches(): Array<HTMLElement> {
    this.select.triggerMenu();
    return this.select.getOptions();
  }

  clickOnChevron() {
    this.chevron.click();
    this.fixture.detectChanges();
  }

  clickOnItem() {
    this.label.click();
    this.fixture.detectChanges();
  }

  selectBranch(id: string) {
    this.select.triggerMenu();
    return this.select.selectOptionByKey(id);
  }
}
