import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {GenerateComponent} from "./documentation/generate/generate.component";
import {AdminPageComponent} from "./admin/admin-page/admin-page.component";


const routes: Routes = [
  {
    path: '',
    redirectTo: 'app/documentation/generate',
    pathMatch: 'full',
  },
  {
    path: 'app',
    redirectTo: 'app/documentation/generate',
    pathMatch: 'full',
  },
  {
    path: 'app/documentation',
    redirectTo: 'app/documentation/generate',
    pathMatch: 'full',
  },
  {
    path: 'app/documentation/generate',
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
    RouterModule.forRoot(routes)
  ]
})
export class AppRoutingModule {
}
