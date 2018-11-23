import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {GenerateComponent} from './documentation/generate/generate.component';
import {AdminPageComponent} from './next/admin-page/admin-page.component';
import {DashboardPageComponent} from './next/dashboard-page/dashboard-page.component';
import {ProfilePageComponent} from './next/profile-page/profile-page.component';
import {SearchPageComponent} from './next/search-page/search-page.component';


const routes: Routes = [
  {
    path: '',
    redirectTo: 'app/documentation/generate/criterias',
    pathMatch: 'full',
  },
  {
    path: 'app',
    redirectTo: 'app/documentation/generate/criterias',
    pathMatch: 'full',
  },
  {
    path: 'app/documentation/generate',
    redirectTo: 'app/documentation/generate/criterias',
    pathMatch: 'full',
  },
  {
    path: 'app/documentation',
    redirectTo: 'app/documentation/generate/criterias',
    pathMatch: 'full',
  },
  {
    path: 'app/documentation/generate/:type',
    component: GenerateComponent,
    canActivate: []
  },
  {
    path: 'app/admin',
    component: AdminPageComponent,
    canActivate: []
  }
  ,
  {
    path: 'app/dashboard',
    component: DashboardPageComponent,
    canActivate: []
  },
  {
    path: 'app/search',
    component: SearchPageComponent,
    canActivate: []
  },
  {
    path: 'app/profile',
    component: ProfilePageComponent,
    canActivate: []
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
