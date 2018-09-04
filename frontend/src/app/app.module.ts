import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {APIInterceptor} from "./http-interceptor";
import { AppComponent } from './app.component';
import {environment} from "../environments/environment";

const nonProductionProviders = [{
  provide: HTTP_INTERCEPTORS,
  useClass: APIInterceptor,
  multi: true,
}];


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule
  ],
  providers: [
    ...!environment.production ? nonProductionProviders : []
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
