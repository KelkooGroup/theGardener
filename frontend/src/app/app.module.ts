import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {APIInterceptor} from './http-interceptor';
import {AppComponent} from './app.component';
import {environment} from '../environments/environment';
import {HeaderComponent} from './_components/page/header/header.component';
import {AppRoutingModule} from './app-routing.module';
import {MenuComponent} from './_components/page/menu/menu.component';
import {MenuItemComponent} from './_components/page/menu-item/menu-item.component';
import {GenerateComponent} from './documentation/generate/generate.component';
import {PageComponent} from './_components/page/page/page.component';
import {AdminPageComponent} from './next/admin-page/admin-page.component';
import {DashboardPageComponent} from './next/dashboard-page/dashboard-page.component';
import {ProfilePageComponent} from './next/profile-page/profile-page.component';
import {SearchPageComponent} from './next/search-page/search-page.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import {FormsModule} from '@angular/forms';
import {
  MatButtonModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDialogModule,
  MatDividerModule,
  MatFormFieldModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatProgressSpinnerModule,
  MatSelectModule,
  MatTableModule,
  MatTabsModule,
  MatTreeModule,
} from '@angular/material';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatRadioModule} from '@angular/material/radio';
import {MatCardModule} from '@angular/material/card';
import {PanelComponent} from './_components/panel/panel.component';
import {CriteriasComponent} from './documentation/generate/criterias/criterias.component';
import {OutputComponent} from './documentation/generate/output/output.component';
import {CriteriasService} from './_services/criterias.service';
import {DocumentationService} from './_services/documentation.service';
import {CriteriasTreeSelectorComponent} from './documentation/generate/criterias/criterias-tree-selector/criterias-tree-selector.component';
import {DocumentationThemeBookComponent} from './documentation/generate/output/themes/documentation-theme-book/documentation-theme-book.component';
import {DocumentationThemeBookTableComponent} from './documentation/generate/output/themes/documentation-theme-book/documentation-theme-book-table/documentation-theme-book-table.component';
import {FooterComponent} from './_components/page/footer/footer.component';
import {CriteriasBranchFeatureSelectorComponent} from './documentation/generate/criterias/criterias-branch-feature-selector/criterias-branch-feature-selector.component';
import {CriteriasFeatureSelectorComponent} from './documentation/generate/criterias/criterias-feature-selector/criterias-feature-selector.component';
import { DocumentationThemeBookLongTextComponent } from './documentation/generate/output/themes/documentation-theme-book/documentation-theme-book-long-text/documentation-theme-book-long-text.component';
import { NavigatePageComponent } from './documentation/navigate/navigate-page.component';
import { NavigateContentComponent } from './documentation/navigate/navigate-content.component';
import { NavigateTreeComponent } from './documentation/navigate/navigate-tree/navigate-tree.component';
import { NavigateMenuComponent } from './documentation/navigate/navigate-menu/navigate-menu.component';

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
    DocumentationThemeBookComponent,
    DocumentationThemeBookTableComponent,
    DashboardPageComponent,
    ProfilePageComponent,
    SearchPageComponent,
    FooterComponent,
    CriteriasBranchFeatureSelectorComponent,
    CriteriasFeatureSelectorComponent,
    DocumentationThemeBookLongTextComponent,
    NavigatePageComponent,
    NavigateContentComponent,
    NavigateTreeComponent,
    NavigateMenuComponent,
  ],
  imports: [
    HttpClientModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    MatCardModule,
    MatButtonModule,
    MatCheckboxModule,
    MatChipsModule,
    MatDialogModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatListModule,
    MatTableModule,
    MatSelectModule,
    MatRadioModule,
    MatTabsModule,
    MatTreeModule,
    MatIconModule,
    MatProgressSpinnerModule,
    FormsModule,
    NgxJsonViewerModule,
    MatExpansionModule,
    MatProgressBarModule,
  ],
  providers: [
    ...!environment.production ? nonProductionProviders : [],
    CriteriasService,
    DocumentationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
