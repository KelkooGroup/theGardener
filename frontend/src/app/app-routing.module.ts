import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {NavigatePageComponent} from './output/navigate/navigate-page.component';
import {PageContentComponent} from './output/page-content/page-content.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'app/documentation/navigate',
    pathMatch: 'full',
  },
  {
    path: 'app',
    redirectTo: 'app/documentation/navigate',
    pathMatch: 'full',
  },
  {
    path: 'app/documentation',
    redirectTo: 'app/documentation/navigate',
    pathMatch: 'full',
  },
  {
    path: 'app/documentation/navigate/:name',
    component: NavigatePageComponent,
    children: [
      {
        path: ':page',
        component: PageContentComponent,
      }
    ]
  },
];

@NgModule({
  exports: [RouterModule],
  imports: [
    RouterModule.forRoot(routes, {
      scrollPositionRestoration: 'enabled',
      anchorScrolling: 'enabled',
      scrollOffset: [0, 0] // [x, y]
    })
  ]
})
export class AppRoutingModule {
}
