import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NavigatePageComponent } from './output/navigate/navigate-page.component';
import { SearchPageComponent } from './output/search/search-page/search-page.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'app/documentation/navigate',
    pathMatch: 'full'
  },
  {
    path: 'app',
    redirectTo: 'app/documentation/navigate',
    pathMatch: 'full'
  },
  {
    path: 'app/documentation',
    redirectTo: 'app/documentation/navigate',
    pathMatch: 'full'
  },
  {
    path: 'app/documentation/navigate/:nodes',
    component: NavigatePageComponent,
    pathMatch: 'full'
  },
  {
    path: 'app/documentation/navigate/:nodes/:project',
    component: NavigatePageComponent,
    pathMatch: 'full'
  },
  {
    path: 'app/documentation/navigate/:nodes/:project/:branch',
    component: NavigatePageComponent,
    pathMatch: 'full'
  },
  {
    path: 'app/documentation/navigate/:nodes/:project/:branch/:directories',
    component: NavigatePageComponent,
    pathMatch: 'full'
  },
  {
    path: 'app/documentation/navigate/:nodes/:project/:branch/:directories/:page',
    component: NavigatePageComponent,
    pathMatch: 'full'
  },
  {
    path: 'app/documentation/search',
    component: SearchPageComponent,
    pathMatch: 'full'
  }
];

@NgModule({
  exports: [RouterModule],
  imports: [
    RouterModule.forRoot(routes, {
      scrollPositionRestoration: 'enabled',
      anchorScrolling: 'enabled',
      scrollOffset: [0, 0]
    })
  ]
})
export class AppRoutingModule {}
