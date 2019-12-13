import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {APIInterceptor} from './http-interceptor';
import {AppComponent} from './app.component';
import {environment} from '../environments/environment';
import {AppRoutingModule} from './app-routing.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NgxJsonViewerModule} from 'ngx-json-viewer';
import {FormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatChipsModule} from '@angular/material/chips';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDividerModule} from '@angular/material/divider';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatListModule} from '@angular/material/list';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {MatSelectModule} from '@angular/material/select';
import {MatTableModule} from '@angular/material/table';
import {MatTabsModule} from '@angular/material/tabs';
import {MatTreeModule} from '@angular/material/tree';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatRadioModule} from '@angular/material/radio';
import {MatCardModule} from '@angular/material/card';
import {MatSnackBarModule} from '@angular/material';
import {MenuService} from './_services/menu.service';
import {GherkinComponent} from './output/gherkin/gherkin.component';
import {GherkinTableComponent} from './output/gherkin/gherkin-table/gherkin-table.component';
import {FooterComponent} from './_components/page/footer/footer.component';
import {GherkinLongTextComponent} from './output/gherkin/gherkin-long-text/gherkin-long-text.component';
import {NavigatePageComponent} from './output/navigate/navigate-page.component';
import {NavigateContentComponent} from './output/navigate/navigate-content/navigate-content.component';
import {NavigateMenuComponent} from './output/navigate/navigate-menu/navigate-menu.component';
import {NotificationService} from './_services/notification.service';
import {GherkinStepComponent} from './output/gherkin/gherkin-step/gherkin-step.component';
import {HeaderComponent} from './_components/page/header/header.component';
import {CdkAccordionModule} from '@angular/cdk/accordion';
import {NavigateMenuItemComponent} from './output/navigate/navigate-menu/navigate-menu-item/navigate-menu-item.component';
import {PageContentComponent} from './output/page-content/page-content.component';
import {MarkdownModule} from 'ngx-markdown';
import {SafePipe} from './safe.pipe';
import { InternalLinkPipe } from './internal-link.pipe';
import { RemoveHtmlSanitizerPipe } from './removehtmlsanitizer.pipe';
import { AnchorPipe } from './anchor.pipe';

const nonProductionProviders = [{
  provide: HTTP_INTERCEPTORS,
  useClass: APIInterceptor,
  multi: true,
}];


@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    NavigatePageComponent,
    NavigateContentComponent,
    NavigateMenuComponent,
    GherkinComponent,
    GherkinTableComponent,
    GherkinLongTextComponent,
    GherkinStepComponent,
    NavigateMenuItemComponent,
    PageContentComponent,
    SafePipe,
    InternalLinkPipe,
    RemoveHtmlSanitizerPipe,
    AnchorPipe,
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
    MatSnackBarModule,
    CdkAccordionModule,
    MarkdownModule.forRoot(),
  ],
  providers: [
    ...!environment.production ? nonProductionProviders : [],
    MenuService,
    NotificationService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
