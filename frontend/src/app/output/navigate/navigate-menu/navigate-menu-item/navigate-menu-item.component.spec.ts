import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NavigateMenuItemComponent} from './navigate-menu-item.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import {RouterTestingModule} from '@angular/router/testing';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {ActivatedRoute, Router} from '@angular/router';
import {ActivatedRouteStub} from '../../../../_testUtils/activated-route-stub.spec';
import {FormsModule} from '@angular/forms';
import {MatSelectHelper} from '../../../../_testUtils/mat-select-helper.spec';
import {MenuProjectHierarchy, MenuType} from "../../../../_models/menu";

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
    activatedRoute.testParams = { nodes:"_publisher"  };
    component.menuItem = {
      name: 'publisher',
      label: 'Publishers',
      type: 'Node' as MenuType,
      depth: 1,
      children: [],
      route:  {
        nodes: ["publisher", "systems", "services"],
        project: "ecs",
        branch: "_",
        directories: ["Features", "Categories"],
        page: "Model"
      }
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(page.label.textContent).toEqual('Publishers');
    expect(page.chevron).toBeFalsy();
  });

  it('should expand / collapse hierarchy nodes', () => {
    activatedRoute.testParams = {nodes:"_publisher"};
    component.menuItem = {
      name: 'publisher',
      label: 'Publishers',
      type: 'Node' as MenuType,
      depth: 1,
      route:  {
        nodes: ["publisher"],
        project: undefined,
        branch: "_",
        directories: [] as  Array<string>,
        page: undefined
      },
      children: [
        {
          name: 'child1',
          label: 'Child 1',
          type: 'Node' as MenuType,
          depth: 2,
          children: [],
          route:  {
            nodes: ["publisher", "child1"],
            project: undefined,
            branch: "_",
            directories: [] as  Array<string>,
            page: undefined
          }
        },
        {
          name: 'child2',
          label: 'Child 2',
          type: 'Node' as MenuType,
          depth: 2,
          children: [],
          route:  {
            nodes: ["publisher", "child2"],
            project: undefined,
            branch: "_",
            directories: [] as  Array<string>,
            page: undefined
          }
        },
      ]
    };
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(page.label.textContent).toEqual('Publishers');
    expect(component.expanded).toBeTruthy();
    page.clickOnChevron();
    expect(component.expanded).toBeFalsy();
  });

  it('should navigate to item on click', () => {
    activatedRoute.testParams = {nodes: '_publisher'};
    component.menuItem = {
      name: 'publisher',
      label: 'Publishers',
      type: 'Node' as MenuType,
      depth: 1,
      route:  {
        nodes: ["publisher"],
        project: "ecs",
        branch: "_",
        directories: [] as  Array<string>,
        page: "Meta"
      },
      children: [
      ]
    };
    fixture.detectChanges();

    const router: Router = TestBed.get(Router);
    spyOn(router, 'navigateByUrl').and.returnValue(Promise.resolve(true));

    page.clickOnItem();
    expect(router.navigateByUrl).toHaveBeenCalledWith('app/documentation/navigate/_publisher/ecs/_/_/Meta');
  });

  it('should show branches in a select if node is a project', async(() => {
    activatedRoute.testParams = {nodes: '_eng', project: 'suggestionWS'};
    const projectItem: MenuProjectHierarchy = {
      name: 'suggestionWS',
      label: 'Suggestion Webservice',
      type: 'Project' as MenuType,
      stableBranch: 'qa',
      depth: 2,
      route:  {},
      children: [
        {
          name: 'qa',
          label: 'qa',
          type: 'Branch' as MenuType,
          depth: 3,
          children: [],
          route:  {}
        },
        {
          name: 'branch1',
          label: 'Branch 1',
          type: 'Branch' as MenuType,
          depth: 3,
          children: [],
          route:  {}
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
    activatedRoute.testParams = {nodes: '_eng', project: 'suggestionWS'};
    const projectItem: MenuProjectHierarchy = {
      name: 'suggestionWS',
      label: 'Suggestion Webservice',
      type: 'Project' as MenuType,
      stableBranch: 'qa',
      depth: 2,
      route:  {},
      children: [
        {
          name: 'branch1',
          label: 'Branch 1',
          type: 'Branch' as MenuType,
          depth: 3,
          children: [],
          route:  {}
        },
        {
          name: 'qa',
          label: 'qa',
          type: 'Branch' as MenuType,
          depth: 3,
          children: [],
          route:  {}
        },
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    expect(component.selectedBranch.name).toEqual(projectItem.stableBranch);
  });

  it('should select branch specified in URL', () => {
    activatedRoute.testParams = {nodes: '_eng', project: 'suggestionWS', branch: "branch1"};
    const projectItem: MenuProjectHierarchy = {
      name: 'suggestionWS',
      label: 'Suggestion Webservice',
      type: 'Project' as MenuType,
      stableBranch: 'qa',
      depth: 2,
      route: {},
      children: [
        {
          name: 'qa',
          label: 'qa',
          type: 'Branch' as MenuType,
          depth: 3,
          children: [],
          route: {},
        },
        {
          name: 'branch1',
          label: 'Branch 1',
          type: 'Branch' as MenuType,
          depth: 3,
          children: [],
          route: {},
        },
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    expect(component.selectedBranch.name).toEqual('branch1');
  });

   it('should not show branches select if only one branch', () => {
    activatedRoute.testParams = {nodes: '_eng', project: 'suggestionWS', branch: "branch1"};
    const projectItem: MenuProjectHierarchy = {
      name: 'suggestionWS',
      label: 'Suggestion Webservice',
      type: 'Project' as MenuType,
      stableBranch: 'qa',
      depth: 2,
      route: {},
      children: [
        {
          name: 'qa',
          label: 'qa',
          type: 'Branch' as MenuType,
          depth: 3,
          children: [],
          route: {},
        }
      ]
    };
    component.menuItem = projectItem;
    fixture.detectChanges();

    expect(page.branchSelect).toBeFalsy();
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
