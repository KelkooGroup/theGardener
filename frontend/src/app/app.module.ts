import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {APIInterceptor} from "./http-interceptor";
import {AppComponent} from './app.component';
import {environment} from "../environments/environment";
import {HeaderComponent} from './_components/page/header/header.component';
import {AppRoutingModule} from './app-routing.module';
import {MenuComponent} from './_components/page/menu/menu.component';
import {MenuItemComponent} from './_components/page/menu-item/menu-item.component';
import {GenerateComponent} from './documentation/generate/generate.component';
import {PageComponent} from './_components/page/page/page.component';
import {AdminPageComponent} from './admin/admin-page/admin-page.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FormsModule} from '@angular/forms';
import {
  MatButtonModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDialogModule,
  MatDividerModule,
  MatFormFieldModule,
  MatSelectModule,
  MatInputModule,
  MatListModule,
  MatTreeModule,
  MatIconModule,
  MatTabsModule,
  MatProgressSpinnerModule,
} from "@angular/material";
import {PanelComponent} from './_components/panel/panel.component';
import {CriteriasComponent} from './documentation/generate/criterias/criterias.component';
import {OutputComponent} from './documentation/generate/output/output.component';
import {HierarchyService} from "./_services/hierarchy.service";
import {DocumentationService} from "./_services/documentation.service";
import {CriteriasTreeSelectorComponent} from './documentation/generate/criterias/criterias-tree-selector/criterias-tree-selector.component';

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
    AdminPageComponent,
    PanelComponent,
    CriteriasComponent,
    OutputComponent,
    CriteriasTreeSelectorComponent,
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    MatButtonModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDialogModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatSelectModule,
    MatTabsModule,
    MatTreeModule,
    MatIconModule,
    MatProgressSpinnerModule,
    FormsModule
  ],
  providers: [
    ...!environment.production ? nonProductionProviders : [],
    HierarchyService,
    DocumentationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
