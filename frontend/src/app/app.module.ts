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
import {PageComponent} from './_components/page/page/page.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
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
import {HierarchyService} from './_services/hierarchy.service';
import {DocumentationService} from './_services/documentation.service';
import {DocumentationThemeBookComponent} from './documentation/themes/documentation-theme-book/documentation-theme-book.component';
import {DocumentationThemeBookTableComponent} from './documentation/themes/documentation-theme-book/documentation-theme-book-table/documentation-theme-book-table.component';
import {FooterComponent} from './_components/page/footer/footer.component';
import {DocumentationThemeBookLongTextComponent} from './documentation/themes/documentation-theme-book/documentation-theme-book-long-text/documentation-theme-book-long-text.component';
import {NavigatePageComponent} from './documentation/navigate/navigate-page.component';
import {NavigateContentComponent} from './documentation/navigate/navigate-content.component';
import {NavigateMenuComponent} from './documentation/navigate/navigate-menu/navigate-menu.component';

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
    PageComponent,
    PanelComponent,
    DocumentationThemeBookComponent,
    DocumentationThemeBookTableComponent,
    FooterComponent,
    DocumentationThemeBookLongTextComponent,
    NavigatePageComponent,
    NavigateContentComponent,
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
    HierarchyService,
    DocumentationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
