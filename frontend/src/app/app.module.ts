
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {APIInterceptor} from "./http-interceptor";
import { AppComponent } from './app.component';
import {environment} from "../environments/environment";
import { HeaderComponent } from './_components/page/header/header.component';
import {AppRoutingModule} from './app-routing.module';
import { MenuComponent } from './_components/page/menu/menu.component';
import { MenuItemComponent } from './_components/page/menu-item/menu-item.component';
import { GenerateComponent } from './documentation/generate/generate.component';
import { PageComponent } from './_components/page/page/page.component';
import { AdminPageComponent } from './admin/admin-page/admin-page.component';

const nonProductionProviders = [{
  provide: HTTP_INTERCEPTORS,
  useClass: APIInterceptor,
  multi: true,
}];


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    MenuComponent,
    MenuItemComponent,
    GenerateComponent,
    PageComponent,
    AdminPageComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
  ],
  providers: [
    ...!environment.production ? nonProductionProviders : []
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
