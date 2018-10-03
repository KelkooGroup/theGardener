import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {GenerateComponent} from "./documentation/generate/generate.component";
import {AdminPageComponent} from "./admin/admin-page/admin-page.component";


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
