import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AdminPageComponent} from './next/admin-page/admin-page.component';
import {DashboardPageComponent} from './next/dashboard-page/dashboard-page.component';
import {ProfilePageComponent} from './next/profile-page/profile-page.component';
import {NavigatePageComponent} from "./documentation/navigate/navigate-page.component";


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
    path: 'app/documentation/navigate',
    component: NavigatePageComponent,
    canActivate: []
  },
  {
    path: 'app/documentation/navigate/:path',
    component: NavigatePageComponent,
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
