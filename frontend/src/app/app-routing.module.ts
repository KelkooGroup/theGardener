import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
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
    path: 'app/documentation/navigate',
    component: NavigatePageComponent,
    canActivate: []
  },
  {
    path: 'app/documentation/navigate/:path',
    component: NavigatePageComponent,
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
