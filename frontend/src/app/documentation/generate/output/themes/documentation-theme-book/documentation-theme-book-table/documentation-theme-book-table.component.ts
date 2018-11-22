import {Component, Input, OnInit} from '@angular/core';
import {DocumentationStepTable} from "../../../../../../_models/documentation";

@Component({
  selector: 'app-documentation-theme-book-table',
  templateUrl: './documentation-theme-book-table.component.html',
  styleUrls: ['./documentation-theme-book-table.component.scss']
})
export class DocumentationThemeBookTableComponent implements OnInit {

  @Input()
  table: DocumentationStepTable;

  constructor() {
  }

  ngOnInit() {
  }

}
